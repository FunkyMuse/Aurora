package com.funkymuse.aurora.latestbooksdata

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.withContext

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

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 1

        return if (context.isOnline) {
            try {
                withContext(dispatcher) { loadBooks(page) }
            } catch (t: Throwable) {
                return LoadResult.Error(t)
            }
        } else {
            return LoadResult.Error(NoConnectionException())
        }
    }

    private suspend fun loadBooks(page: Int): LoadResult.Page<Int, Book> {
        val list = fetch(page)
        return if (list.isNullOrEmpty()) {
            canNotLoadMoreContent()
        } else {
            val prevKey = if (list.isNotNullOrEmpty) if (page == 1) null else page - 1 else null
            val nextKey = if (list.count() == 0) null else page.plus(1)
            LoadResult.Page(list, prevKey, nextKey)
        }
    }

    private suspend fun fetch(page:Int): List<Book> =
            skrape(HttpFetcher) {
                request {
                    timeout = DEFAULT_API_TIMEOUT
                    url = "$SEARCH_BASE_URL?$SORT_QUERY=$sortQuery&$VIEW_QUERY=$VIEW_QUERY_PARAM&$RES_CONST=$PAGE_SIZE&" +
                                "$LAST_MODE=$LAST_QUERY&$COLUM_QUERY=$FIELD_DEFAULT_PARAM&$SORT_TYPE=$sortType&"+
                            "$PAGE_CONST=$page"
                    Log.d("URL REQUESt", url)

                }
                response {
                    htmlDocument {
                        findAll("table").asSequence().drop(2).map {

                            val elementList =
                                tryOrNull { it.findAll("tr").filter { it.children.size >= 2 } }
                                    ?.map { it.findAll("td") }?.flatten()?.map { it.children }
                                    ?.flatten()


                            val res = if (!elementList.isNullOrEmpty()) {
                                elementList.mapNotNull {

                                    val id = tryOrNull {
                                        elementList[2].eachLink.values.firstOrNull()?.substringAfter("md5=")
                                    }
                                    if (id == null) {
                                        null
                                    } else {
                                        Book(
                                            image = tryOrNull { elementList[0].eachImage.values.firstOrNull() },
                                            title = tryOrNull { elementList[2].text },
                                            author = tryOrNull { elementList[5].text },
                                            id = id
                                        )
                                    }
                                }
                            } else {
                                emptyList()
                            }

                            res
                        }.flatten().toSet().toList()
                    }
                }
            }

    private fun <T> tryOrNull(block: () -> T) = try {
        block()
    } catch (t: Throwable) {
        null
    }
}