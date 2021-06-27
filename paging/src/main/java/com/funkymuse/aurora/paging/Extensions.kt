package com.funkymuse.aurora.paging

import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */


val LazyPagingItems<*>.appendState get() = loadState.append
val LazyPagingItems<*>.refreshState get() = loadState.refresh
val LazyPagingItems<*>.prependState get() = loadState.prepend

fun <T : Any> canNotLoadMoreContent(): PagingSource.LoadResult.Page<Int, T> =
        PagingSource.LoadResult.Page(emptyList(), null, null)