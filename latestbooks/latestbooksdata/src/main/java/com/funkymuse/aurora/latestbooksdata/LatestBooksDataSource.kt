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
import com.funkymuse.aurora.serverconstants.COLUM_QUERY
import com.funkymuse.aurora.serverconstants.SORT_TYPE
import com.funkymuse.aurora.skraper.BookScraper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Created by funkymuse on 5/10/21 to long live and prosper !
 */

class LatestBooksDataSource @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted(COLUM_QUERY) private val sortQuery: String,
    @Assisted(SORT_TYPE) private val sortType: String,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val scraper: BookScraper
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
        val list = scraper.fetch(scraper.generateLatestBooksUrl(page, sortQuery, sortType))
        return if (list.isNullOrEmpty()) {
            canNotLoadMoreContent()
        } else {
            val prevKey = if (list.isNotNullOrEmpty) if (page == 1) null else page - 1 else null
            val nextKey = if (list.count() == 0) null else page.plus(1)
            LoadResult.Page(list, prevKey, nextKey)
        }
    }

}



