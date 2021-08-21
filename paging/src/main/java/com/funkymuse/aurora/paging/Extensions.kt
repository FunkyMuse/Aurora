package com.funkymuse.aurora.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import com.crazylegend.common.isOnline
import com.crazylegend.retrofit.throwables.NoConnectionException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */


val LazyPagingItems<*>.appendState get() = loadState.append
val LazyPagingItems<*>.refreshState get() = loadState.refresh
val LazyPagingItems<*>.prependState get() = loadState.prepend

fun <T : Any> canNotLoadMoreContent(): PagingSource.LoadResult.Page<Int, T> =
    PagingSource.LoadResult.Page(emptyList(), null, null)

suspend fun <T : Any> fetchPaginatedContent(
    context: Context,
    dispatcher: CoroutineDispatcher,
    params: PagingSource.LoadParams<Int>,
    loadBooks: (page: Int) -> PagingSource.LoadResult<Int, T>
): PagingSource.LoadResult<Int, T> {
    val page = params.key ?: 1

    return if (context.isOnline) {
        try {
            withContext(dispatcher) { loadBooks(page) }
        } catch (throwable: Throwable) {
            return PagingSource.LoadResult.Error(throwable)
        }
    } else {
        return PagingSource.LoadResult.Error(NoConnectionException())
    }
}

fun <T : Any> pagedResult(
    list: List<T>,
    page: Int
): PagingSource.LoadResult.Page<Int, T> {
    return if (list.isNullOrEmpty()) {
        canNotLoadMoreContent()
    } else {
        val prevKey = if (!list.isNullOrEmpty()) if (page == 1) null else page - 1 else null
        val nextKey = if (list.count() == 0) null else page.plus(1)
        PagingSource.LoadResult.Page(list, prevKey, nextKey)
    }
}


/*
val lifecycle = LocalLifecycleOwner.current.lifecycle
val favorites = remember(pagingData, lifecycle) {
        pagingData.flowWithLifecycle(lifecycle)
}.collectAsLazyPagingItems()*/
