package com.funkymuse.aurora.bottomNav.search

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crazylegend.kotlinextensions.context.isOnline
import com.crazylegend.kotlinextensions.livedata.context
import com.crazylegend.retrofit.retrofitResult.*
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.consts.*
import com.funkymuse.aurora.dto.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by FunkyMuse, date 2/27/21
 */
class SearchViewModel(
    private val searchQuery: String,
    private val sortQuery: String = "",
    private val sortType: String = "",
    application: Application

) : AndroidViewModel(application) {

    private val retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>> =
        MutableStateFlow(RetrofitResult.EmptyData)
    val booksData = retrofitResult.asStateFlow()

    private var page = mutableStateOf(1)
    private var canLoadMore = mutableStateOf(false)
    private val adapterList = mutableStateListOf<Book>()

    private fun getData(): Document? {
        val jsoup = Jsoup.connect(SEARCH_BASE_URL)
            .timeout(DEFAULT_API_TIMEOUT)
            .data(REQ_CONST, searchQuery)
            .data(SORT_QUERY, sortQuery)
            .data(VIEW_QUERY, VIEW_QUERY_PARAM)
            .data(LAST_MODE, LAST_QUERY)
            .data(COLUM_QUERY, COLUMN_QUERY_PARAM)
            .data(RES_CONST, PAGE_SIZE)
            .data(SORT_TYPE, sortType)
            .data(PAGE_CONST, page.toString())
        return jsoup.get()
    }

    init {
        searchForBook()
    }

    fun searchForBook() {
        if (canLoadMore.value) {
            retrofitResult.loading()
            if (context.isOnline) {
                viewModelScope.launch {
                    try {
                        val it = withContext(Dispatchers.Default) { getData() }
                        if (it == null) {
                            retrofitResult.emptyData()
                        } else {
                            val list = processDocument(it)
                            if (list.isNullOrEmpty()) {
                                retrofitResult.emptyData()
                            } else {
                                adapterList += list
                                retrofitResult.success(adapterList)
                            }
                        }
                    } catch (t: Throwable) {
                        retrofitResult.callError(t)
                    }
                }
            } else {
                retrofitResult.callError(NoConnectionException())
            }
        }
    }


    private fun processDocument(doc: Document?): List<Book>? {
        return doc?.let { document ->

            val trs = document.select("table")[2].select("tr")
            trs.removeAt(0)

            if (trs.size < PAGE_SIZE.toInt()) {
                //cant load more
                canLoadMore.value = false
            } else {
                page.value++
                //has more pages
            }

            return@let trs.map {
                return@map Book(it)
            }
        }
    }
}