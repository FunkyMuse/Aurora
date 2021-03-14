package com.funkymuse.aurora.searchResult

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.crazylegend.kotlinextensions.context.isOnline
import com.crazylegend.kotlinextensions.internetdetector.InternetDetector
import com.crazylegend.kotlinextensions.livedata.context
import com.crazylegend.kotlinextensions.log.debug
import com.crazylegend.retrofit.retrofitResult.*
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.consts.*
import com.funkymuse.aurora.dto.Book
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

/**
 * Created by funkymuse on 3/8/21 to long live and prosper !
 */

class SearchResultVM @AssistedInject constructor(
    internetDetector: InternetDetector,
    application: Application,
    @Assisted private val searchQuery: String,
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted(SEARCH_IN_CHECKED_POSITION_KEY) private val searchInCheckedPosition: Int,
    @Assisted(SEARCH_IN_FIELDS_CHECKED_POSITION_KEY) private val searchInFieldsCheckedPosition: Int,
    @Assisted private val searchWithMaskWord: Boolean
) : AndroidViewModel(application) {

    private companion object {
        private const val SEARCH_IN_CHECKED_POSITION_KEY = "searchInCheckedPosition"
        private const val SEARCH_IN_FIELDS_CHECKED_POSITION_KEY = "searchInFieldsCheckedPosition"
        private const val SEARCH_WITH_MASKED_WORD_KEY = "searchWithMaskWord"
        private const val SORT_QUERY_KEY = "sortQuery"
        private const val SORT_TYPE_KEY = "sortType"
    }

    @AssistedFactory
    interface SearchResultVMF {
        fun create(
            searchQuery: String,
            savedStateHandle: SavedStateHandle,
            @Assisted(SEARCH_IN_CHECKED_POSITION_KEY) searchInCheckedPosition: Int,
            @Assisted(SEARCH_IN_FIELDS_CHECKED_POSITION_KEY) searchInFieldsCheckedPosition: Int,
            searchWithMaskWord: Boolean
        ): SearchResultVM
    }

    val internetConnection = internetDetector.state
    private val booksDataHolder: MutableStateFlow<RetrofitResult<List<Book>>> =
        MutableStateFlow(RetrofitResult.EmptyData)
    val booksData = booksDataHolder.asStateFlow()

    private var page = 1
    private var canLoadMore = true
    private val adapterList = mutableStateListOf<Book>()

    private var maskWord
        get() = savedStateHandle[SEARCH_WITH_MASKED_WORD_KEY] ?: searchWithMaskWord
        set(value) {
            setMaskWordHandle(value)
        }

    private fun setMaskWordHandle(value: Boolean) {
        savedStateHandle[SEARCH_WITH_MASKED_WORD_KEY] = value
    }

    private var sortQuery
        get() = savedStateHandle[SORT_QUERY_KEY] ?: ""
        set(value) {
            setSortQueryHandle(value)
        }

    private fun setSortQueryHandle(query: String) {
        savedStateHandle[SORT_QUERY_KEY] = query
    }

    private var sortType
        get() = savedStateHandle[SORT_TYPE_KEY] ?: ""
        set(value) {
            setSortTypeHandle(value)
        }

    private fun setSortTypeHandle(type: String) {
        savedStateHandle[SORT_TYPE_KEY] = type
    }

    init {
        searchForBook()
    }

    fun searchForBook() {
        if (canLoadMore) {
            booksDataHolder.loading()
            if (context.isOnline) {
                viewModelScope.launch {
                    try {
                        val it = withContext(Dispatchers.Default) { getData() }
                        if (it == null) {
                            booksDataHolder.emptyData()
                        } else {
                            val list = processDocument(it)
                            if (list.isNullOrEmpty()) {
                                booksDataHolder.emptyData()
                            } else {
                                adapterList += list
                                booksDataHolder.success(adapterList)
                            }
                        }
                    } catch (t: Throwable) {
                        booksDataHolder.callError(t)
                    }
                }
            } else {
                booksDataHolder.callError(NoConnectionException())
            }
        }
    }

    private fun getData(): Document? {
        val jsoup = Jsoup.connect(SEARCH_BASE_URL)
            .data(REQ_CONST, searchQuery)
            .timeout(DEFAULT_API_TIMEOUT)
            .data(SORT_QUERY, sortQuery)
            .data(VIEW_QUERY, VIEW_QUERY_PARAM)
            .data(SEARCH_WITH_MASK, if (searchWithMaskWord) SEARCH_WITH_MASK_YES else SEARCH_WITH_MASK_NO)
            .data(COLUM_QUERY, COLUMN_QUERY_PARAM)
            .data(RES_CONST, PAGE_SIZE)
            .data(SORT_TYPE, sortType)
            .data(PAGE_CONST, page.toString())
        val req = jsoup.get()
        debug("URL ${req.location()}")
        return req
    }


    private fun processDocument(doc: Document?): List<Book>? {
        return doc?.let { document ->

            val trs = document.select("table")[2].select("tr")
            trs.removeAt(0)

            if (trs.size < PAGE_SIZE.toInt()) {
                //cant load more
                canLoadMore = false
            } else {
                page++
                //has more pages
            }

            return@let trs.map {
                return@map Book(it)
            }
        }
    }

    private fun resetOnSort() {
        adapterList.clear()
        sortType = ""
        sortQuery = ""
        page = 1
        canLoadMore = true
    }

    fun sortByYearDESC() {
        resetOnSort()
        sortType = SORT_YEAR_CONST
        sortQuery = SORT_TYPE_DESC
        searchForBook()
    }

    fun sortByYearASC() {
        resetOnSort()
        sortType = SORT_YEAR_CONST
        sortQuery = SORT_TYPE_ASC
        searchForBook()
    }

    fun sortByDefault() {
        resetOnSort()
        searchForBook()
    }

    fun sortBySizeDESC() {
        resetOnSort()
        sortType = SORT_SIZE
        sortQuery = SORT_TYPE_DESC
        searchForBook()
    }

    fun sortBySizeASC() {
        resetOnSort()
        sortType = SORT_SIZE
        sortQuery = SORT_TYPE_ASC
        searchForBook()
    }

    fun refresh() {
        resetOnSort()
        searchForBook()
    }


}