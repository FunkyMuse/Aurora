package com.funkymuse.aurora

import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import com.funkymuse.aurora.dto.Book

/**
 * Created by funkymuse on 5/11/21 to long live and prosper !
 */

fun canNotLoadMoreBooks(): PagingSource.LoadResult.Page<Int, Book> =
    PagingSource.LoadResult.Page(emptyList(), null, null)


val LazyPagingItems<*>.appendState get() = loadState.append
val LazyPagingItems<*>.refreshState get() = loadState.refresh
val LazyPagingItems<*>.prependState get() = loadState.prepend