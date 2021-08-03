package com.funkymuse.aurora.paging

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */


val LazyPagingItems<*>.appendState get() = loadState.append
val LazyPagingItems<*>.refreshState get() = loadState.refresh
val LazyPagingItems<*>.prependState get() = loadState.prepend

fun <T : Any> canNotLoadMoreContent(): PagingSource.LoadResult.Page<Int, T> =
        PagingSource.LoadResult.Page(emptyList(), null, null)


/*
val lifecycle = LocalLifecycleOwner.current.lifecycle
val favorites = remember(pagingData, lifecycle) {
        pagingData.flowWithLifecycle(lifecycle)
}.collectAsLazyPagingItems()*/
