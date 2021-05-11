package com.funkymuse.aurora.latestBooks

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crazylegend.kotlinextensions.collections.isNotNullOrEmpty
import com.crazylegend.kotlinextensions.context.isOnline
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.consts.*
import com.funkymuse.aurora.dto.Book
import com.funkymuse.aurora.extensions.canNotLoadMoreBooks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by funkymuse on 5/10/21 to long live and prosper !
 */
class LatestBooksDataSource(
    private val context: Context,
    private val sortQuery: String = "",
    private val sortType: String = "",
) : PagingSource<Int, Book>() {

    var canLoadMore = true

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 1

        return if (context.isOnline) {
            try {
                val it = withContext(Dispatchers.IO) { getData(page) }
                if (it == null) {
                    return canNotLoadMoreBooks()
                } else {
                    if (canLoadMore) {
                        val list = processDocument(it)
                        if (list.isNullOrEmpty()) {
                            return canNotLoadMoreBooks()
                        } else {
                            val prevKey =
                                if (list.isNotNullOrEmpty) if (page == 1) null else page - 1 else null
                            val nextKey = if (list.count() == 0) null else page.plus(1)
                            LoadResult.Page(list, prevKey, nextKey)
                        }
                    } else {
                        canNotLoadMoreBooks()
                    }
                }
            } catch (t: Throwable) {
                return LoadResult.Error(t)
            }
        } else {
            return LoadResult.Error(NoConnectionException())
        }

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


    private fun getData(page: Int): Document? {
        val jsoup = Jsoup.connect(SEARCH_BASE_URL)
            .timeout(DEFAULT_API_TIMEOUT)
            .data(SORT_QUERY, sortQuery)
            .data(VIEW_QUERY, VIEW_QUERY_PARAM)
            .data(LAST_MODE, LAST_QUERY)
            .data(COLUM_QUERY, FIELD_DEFAULT_PARAM)
            .data(RES_CONST, PAGE_SIZE)
            .data(SORT_TYPE, sortType)
            .data(PAGE_CONST, page.toString())
        return jsoup.get()
    }
}