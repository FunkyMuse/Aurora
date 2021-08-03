package com.funkymuse.aurora.searchresultdata

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crazylegend.collections.isNotNullOrEmpty
import com.crazylegend.common.isOnline
import com.crazylegend.common.tryOrNull
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.bookmodel.Book
import com.funkymuse.aurora.dispatchers.IoDispatcher
import com.funkymuse.aurora.paging.canNotLoadMoreContent
import com.funkymuse.aurora.serverconstants.*
import com.funkymuse.aurora.skraper.BookScraper
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
 * Created by funkymuse on 5/11/21 to long live and prosper !
 */
class SearchResultDataSource @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted(COLUM_QUERY) private val searchQuery: String,
    @Assisted(FIELDS_QUERY_CONST) private val searchInFieldsPosition: Int,
    @Assisted(SORT_QUERY) private val sortQuery: String,
    @Assisted(SEARCH_WITH_MASK) private val maskWord: Boolean,
    @Assisted(SORT_TYPE) private val sortType: String,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val scraper:BookScraper
) : PagingSource<Int, Book>() {

    @AssistedFactory
    interface SearchResultDataSourceFactory {
        fun create(
            @Assisted(COLUM_QUERY) searchQuery: String,
            @Assisted(FIELDS_QUERY_CONST) searchInFieldsPosition: Int,
            @Assisted(SORT_QUERY) sortQuery: String,
            @Assisted(SEARCH_WITH_MASK) maskWord: Boolean,
            @Assisted(SORT_TYPE) sortType: String
        ): SearchResultDataSource
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
        val list = scraper.fetch(scraper.generateSearchDataUrl(page, searchQuery, sortQuery, sortType, searchInFieldsPosition, maskWord))
        return if (list.isNullOrEmpty()) {
            canNotLoadMoreContent()
        } else {
            val prevKey = if (list.isNotNullOrEmpty) if (page == 1) null else page - 1 else null
            val nextKey = if (list.count() == 0) null else page.plus(1)
            LoadResult.Page(list, prevKey, nextKey)
        }
    }

}