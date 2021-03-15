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
    @Assisted(SEARCH_IN_FIELDS_CHECKED_POSITION_KEY) private val searchInFieldsCheckedPosition: Int,
    @Assisted private val searchWithMaskWord: Boolean
) : AndroidViewModel(application) {

    private companion object {
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

    private var searchInFieldsPosition
        get() = savedStateHandle[SEARCH_IN_FIELDS_CHECKED_POSITION_KEY]
            ?: searchInFieldsCheckedPosition
        set(value) {
            setSearchInFieldPositionState(value)
        }

    private fun setSearchInFieldPositionState(value: Int) {
        savedStateHandle[SEARCH_IN_FIELDS_CHECKED_POSITION_KEY] = value
    }

    private var maskWord
        get() = savedStateHandle[SEARCH_WITH_MASKED_WORD_KEY] ?: searchWithMaskWord
        set(value) {
            setMaskWordHandle(value)
        }

    private fun setMaskWordHandle(value: Boolean) {
        savedStateHandle[SEARCH_WITH_MASKED_WORD_KEY] = value
    }

    private var sortType
        get() = savedStateHandle[SORT_TYPE_KEY] ?: ""
        set(value) {
            setSortTypeHandle(value)
        }

    private fun setSortTypeHandle(type: String) {
        savedStateHandle[SORT_TYPE_KEY] = type
    }


    private var sortQuery
        get() = savedStateHandle[SORT_QUERY_KEY] ?: ""
        set(value) {
            setSortQueryHandle(value)
        }
    private fun setSortQueryHandle(query: String) {
        savedStateHandle[SORT_QUERY_KEY] = query
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
        debug("SORT BY $sortQuery")
        val jsoup = Jsoup.connect(SEARCH_BASE_URL)
            .timeout(DEFAULT_API_TIMEOUT)
            .data(REQ_CONST, searchQuery)
            .data(VIEW_QUERY, VIEW_QUERY_PARAM)
            .data(COLUM_QUERY, getFieldParamByPosition(searchInFieldsPosition))
            .data(SEARCH_WITH_MASK, if (maskWord) SEARCH_WITH_MASK_YES else SEARCH_WITH_MASK_NO)
            .data(RES_CONST, PAGE_SIZE)
            .data(SORT_QUERY, sortQuery)
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
        sortQuery = ""
        sortType = ""
        page = 1
        canLoadMore = true
    }

    private fun sortByYearDESC() {
        resetOnSort()
        sortQuery = SORT_YEAR_CONST
        sortType = SORT_TYPE_DESC
        searchForBook()
    }

    private fun sortByYearASC() {
        resetOnSort()
        sortQuery = SORT_YEAR_CONST
        sortType = SORT_TYPE_ASC
        searchForBook()
    }

    private fun sortByDefault() {
        resetOnSort()
        searchForBook()
    }

    private fun sortBySizeDESC() {
        resetOnSort()
        sortQuery = SORT_SIZE
        sortType = SORT_TYPE_DESC
        searchForBook()
    }

    private fun sortBySizeASC() {
        resetOnSort()
        sortQuery = SORT_SIZE
        sortType = SORT_TYPE_ASC
        searchForBook()
    }

    fun refresh() {
        resetOnSort()
        searchForBook()
    }

    /**
     *  Pair(0, R.string.default_sort),
    Pair(1, R.string.year_asc),
    Pair(2, R.string.year_desc),
    Pair(3, R.string.size_asc),
    Pair(4, R.string.size_desc),
    Pair(5, R.string.author_asc),
    Pair(6, R.string.author_desc),
    Pair(7, R.string.title_asc),
    Pair(8, R.string.title_desc),
    Pair(9, R.string.extension_asc),
    Pair(10, R.string.extension_desc),
    Pair(11, R.string.publisher_asc),
    Pair(12, R.string.publisher_desc),
     * @param position Int
     */
    fun sortByPosition(position: Int) {
        when (position) {
            0 -> sortByDefault()
            1 -> sortByYearASC()
            2 -> sortByYearDESC()
            3 -> sortBySizeASC()
            4 -> sortBySizeDESC()
            5 -> sortByAuthorASC()
            6 -> sortByAuthorDESC()
            7 -> sortByTitleASC()
            8 -> sortByTitleDESC()
            9 -> sortByExtensionASC()
            10 -> sortByExtensionDESC()
            11 -> sortByPublisherASC()
            12 -> sortByPublisherDESC()
        }
    }

    private fun sortByPublisherASC() {
        resetOnSort()
        sortQuery = SORT_PUBLISHER
        sortType = SORT_TYPE_ASC
        searchForBook()
    }

    private fun sortByPublisherDESC() {
        resetOnSort()
        sortQuery = SORT_PUBLISHER
        sortType = SORT_TYPE_DESC
        searchForBook()
    }

    private fun sortByExtensionASC() {
        resetOnSort()
        sortQuery = SORT_EXTENSION
        sortType = SORT_TYPE_ASC
        searchForBook()
    }

    private fun sortByExtensionDESC() {
        resetOnSort()
        sortQuery = SORT_EXTENSION
        sortType = SORT_TYPE_DESC
        searchForBook()
    }

    private fun sortByTitleASC() {
        resetOnSort()
        sortQuery = SORT_TITLE
        sortType = SORT_TYPE_ASC
        searchForBook()
    }

    private fun sortByTitleDESC() {
        resetOnSort()
        sortQuery = SORT_TITLE
        sortType = SORT_TYPE_DESC
        searchForBook()
    }

    private fun sortByAuthorDESC() {
        resetOnSort()
        sortQuery = SORT_AUTHOR
        sortType = SORT_TYPE_DESC
        searchForBook()
    }

    private fun sortByAuthorASC() {
        resetOnSort()
        sortQuery = SORT_AUTHOR
        sortType = SORT_TYPE_ASC
        searchForBook()
    }


}