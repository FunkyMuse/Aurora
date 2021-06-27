package com.funkymuse.aurora.searchresultdata

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crazylegend.collections.isNotNullOrEmpty
import com.crazylegend.common.isOnline
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.bookmodel.Book
import com.funkymuse.aurora.paging.canNotLoadMoreContent
import com.funkymuse.aurora.serverconstants.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by funkymuse on 5/11/21 to long live and prosper !
 */
class SearchResultDataSource(
        private val context: Context,
        private val searchQuery: String,
        private val searchInFieldsPosition: Int,
        private val sortQuery: String,
        private val maskWord: Boolean,
        private val sortType: String
) : PagingSource<Int, Book>() {


    var canLoadMore = true

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {

        val page = params.key ?: 1

        return if (context.isOnline) {
            try {
                val it = withContext(Dispatchers.IO) { getData(page) }
                if (it == null) {
                    canNotLoadMoreContent()
                } else {
                    tryToLoadBooks(page, it)
                }
            } catch (t: Throwable) {
                return LoadResult.Error(t)
            }
        } else {
            return LoadResult.Error(NoConnectionException())
        }
    }

    private fun tryToLoadBooks(page: Int, it: Document): LoadResult.Page<Int, Book> {
        return if (canLoadMore) {
            loadBooks(it, page)
        } else {
            canNotLoadMoreContent()
        }
    }

    private fun loadBooks(it: Document, page: Int): LoadResult.Page<Int, Book> {
        val list = processDocument(it)
        return if (list.isNullOrEmpty()) {
            canNotLoadMoreContent()
        } else {
            val prevKey =
                    if (list.isNotNullOrEmpty) if (page == 1) null else page - 1 else null
            val nextKey = if (list.count() == 0) null else page.plus(1)
            LoadResult.Page(list, prevKey, nextKey)
        }
    }


    private fun getData(page: Int): Document? {
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

        return jsoup.get()
    }

    private fun processDocument(doc: Document?): List<Book>? {
        return doc?.let { document ->

            val trs = document.select("table")[2].select("tr")
            trs.removeAt(0)

            if (trs.size < PAGE_SIZE.toInt()) {
                //cant load more
                canLoadMore = false
            }

            return@let trs.map {
                return@map Book(it)
            }
        }
    }
}