package com.funkymuse.aurora.repositories

import com.crazylegend.retrofit.retrofitResult.*
import com.funkymuse.aurora.consts.*
import com.funkymuse.aurora.dto.Book
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by FunkyMuse on 10/21/19 to long live and prosper !
 */
@Singleton
class LatestBooksRepo @Inject constructor() {

    private var page = 1
    private var canLoadMore = true
    private var sortQuery = ""
    private var sortType = ""
    private val adapterList: ArrayList<Book> = ArrayList()

    fun searchForBook(
        scope: CoroutineScope,
        retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>>
    ) {
        if (canLoadMore) {
            retrofitResult.loading()
            scope.launch {
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
        }
    }

    private fun getData(): Document? {
        val jsoup = Jsoup.connect(SEARCH_BASE_URL)
            .timeout(DEFAULT_API_TIMEOUT)
            .data(SORT_QUERY, sortQuery)
            .data(VIEW_QUERY, VIEW_QUERY_PARAM)
            .data(LAST_MODE, LAST_QUERY)
            .data(COLUM_QUERY, COLUMN_QUERY_PARAM)
            .data(RES_CONST, PAGE_SIZE)
            .data(SORT_TYPE, sortType)
            .data(PAGE_CONST, page.toString())
        return jsoup.get()
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

    fun sortByYearDESC(
        scope: CoroutineScope,
        retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>>
    ) {
        resetOnSort()
        sortType = SORT_YEAR_CONST
        sortQuery = SORT_TYPE_DESC
        searchForBook(scope, retrofitResult)
    }

    fun sortByYearASC(
        scope: CoroutineScope,
        retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>>
    ) {
        resetOnSort()
        sortType = SORT_YEAR_CONST
        sortQuery = SORT_TYPE_ASC
        searchForBook(scope, retrofitResult)
    }

    fun sortByDefault(
        scope: CoroutineScope,
        retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>>
    ) {
        resetOnSort()
        searchForBook(scope, retrofitResult)
    }

    fun sortBySizeDESC(
        scope: CoroutineScope,
        retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>>
    ) {
        resetOnSort()
        sortType = SORT_SIZE
        sortQuery = SORT_TYPE_DESC
        searchForBook(scope, retrofitResult)
    }

    fun sortBySizeASC(
        scope: CoroutineScope,
        retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>>
    ) {
        resetOnSort()
        sortType = SORT_SIZE
        sortQuery = SORT_TYPE_ASC
        searchForBook(scope, retrofitResult)
    }

    fun refresh(
        scope: CoroutineScope,
        retrofitResult: MutableStateFlow<RetrofitResult<List<Book>>>
    ) {
        resetOnSort()
        searchForBook(scope, retrofitResult)
    }


}

