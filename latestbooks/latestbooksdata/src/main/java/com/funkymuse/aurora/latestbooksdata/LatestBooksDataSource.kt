package com.funkymuse.aurora.latestbooksdata

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crazylegend.collections.isNotNullOrEmpty
import com.crazylegend.common.isOnline
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.bookmodel.Book
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.paging.canNotLoadMoreContent
import com.funkymuse.aurora.serverconstants.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by funkymuse on 5/10/21 to long live and prosper !
 */

class LatestBooksDataSource @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted(COLUM_QUERY) private val sortQuery: String,
    @Assisted(SORT_TYPE) private val sortType: String,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : PagingSource<Int, Book>() {

    @AssistedFactory
    interface LatestBookDataSourceFactory {
        fun create(
            @Assisted(COLUM_QUERY) sortQuery: String,
            @Assisted(SORT_TYPE) sortType: String,
        ): LatestBooksDataSource
    }

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
            val prevKey = if (list.isNotNullOrEmpty) if (page == 1) null else page - 1 else null
            val nextKey = if (list.count() == 0) null else page.plus(1)
            LoadResult.Page(list, prevKey, nextKey)
        }
    }


    private suspend fun fetch(): List<Book> =
        withContext(Dispatchers.IO) {
            skrape(HttpFetcher) {
                request {
                    timeout = DEFAULT_API_TIMEOUT
                    url =
                        "$SEARCH_BASE_URL?req=dan+brown&lg_topic=libgen&view=detailed&res=100&phrase=1&column=def"
                }
                response {
                    htmlDocument {
                        findAll("table").drop(2).map {

                            val trs =
                                tryOrNull { it.findAll("tr").filter { it.children.size >= 2 } }
                                    ?.map { it.findAll("td") }?.flatten()?.map { it.children }
                                    ?.flatten()

                            val res = if (!trs.isNullOrEmpty()) {
                                trs.dropLast(1).mapNotNull {
                                    val id = tryOrNull {
                                        trs[2].eachLink.values.firstOrNull()?.substringAfter("md5=")
                                    }
                                    if (id == null) {
                                        null
                                    } else {
                                        Book(
                                            image = tryOrNull { trs[0].eachImage.values.firstOrNull() },
                                            title = tryOrNull { trs[2].text },
                                            author = tryOrNull { trs[5].text },
                                            id = id
                                        )
                                    }
                                }
                            } else {
                                emptyList()
                            }

                            res
                        }.flatten()
                    }
                }
            }
        }

    private fun <T> tryOrNull(block: () -> T) = try {
        block()
    } catch (t: Throwable) {
        null
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