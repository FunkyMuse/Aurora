package com.funkymuse.aurora.paging.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.crazylegend.retrofit.throwables.NoConnectionException
import kotlinx.coroutines.CoroutineScope

/**
 * Created by funkymuse on 5/11/21 to long live and prosper !
 */
interface PagingUIProviderContract {

    fun isDataEmptyWithError(
        append: LoadState,
        refresh: LoadState,
        itemCount: Int
    ): Boolean

    fun <T : Any> isDataEmpty(pagingItems: LazyPagingItems<T>): Boolean

    fun isDataEmptyWithError(
        refresh: LoadState,
        append: LoadState,
        prepend: LoadState,
        itemCount: Int
    ): Boolean

    fun progressBarVisibility(
        append: LoadState,
        refresh: LoadState
    ): Boolean

    fun isSwipeToRefreshEnabled(append: LoadState, refresh: LoadState): Boolean
    fun onPaginationReachedError(append: LoadState, @StringRes errorMessage: Int)

    @Composable
    fun <T : Any> OnError(
        refresh: LoadState,
        append: LoadState,
        prepend: LoadState,
        scope: CoroutineScope,
        pagingItems: LazyPagingItems<T>,
        noInternetUI: @Composable () -> Unit = {},
        errorUI: @Composable () -> Unit
    )

    fun isLoadStateNoConnectionException(state: LoadState): Boolean =
        state is LoadState.Error && state.error is NoConnectionException


    fun isLoadStateError(state: LoadState): Boolean =
        state is LoadState.Error
}