package com.funkymuse.aurora.paging.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.crazylegend.internetdetector.InternetDetector
import com.crazylegend.toaster.Toaster
import com.funkymuse.aurora.paging.R
import com.funkymuse.aurora.paging.appendState
import com.funkymuse.aurora.paging.prependState
import com.funkymuse.aurora.paging.refreshState
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 * Created by funkymuse on 5/10/21 to long live and prosper !
 */
@ViewModelScoped
class PagingUIProvider @Inject constructor(
        private val toaster: Toaster,
        private val internetDetector: InternetDetector
) : PagingUIProviderContract {

    override fun <T : Any> isDataEmpty(pagingItems: LazyPagingItems<T>): Boolean =
            pagingItems.itemCount == 0

    @Composable
    override fun <T : Any> OnError(scope: CoroutineScope, pagingItems: LazyPagingItems<T>, noInternetUI: () -> Unit, errorUI: () -> Unit) {
        val append = pagingItems.appendState
        val refresh = pagingItems.refreshState
        val prepend = pagingItems.prependState
        if (isLoadStateNoConnectionException(refresh) ||
                isLoadStateNoConnectionException(append) ||
                isLoadStateNoConnectionException(prepend)
        ) {

            if (pagingItems.itemCount == 0) {
                noInternetUI()
            } else {
                toaster.longToast(R.string.no_connection_message_will_load_on_connection_achieved)
            }

            LaunchedEffect(scope) {
                internetDetector.state.collectLatest {
                    if (it) pagingItems.retry()
                }
            }

        } else {
            if (isDataEmptyWithError(pagingItems)) {
                errorUI()
            }
        }
    }

    override fun <T : Any> isDataEmptyWithError(pagingItems: LazyPagingItems<T>): Boolean {
        val append = pagingItems.appendState
        val refresh = pagingItems.refreshState
        val prepend = pagingItems.prependState
        val itemCount = pagingItems.itemCount
        return ((append is LoadState.Error || refresh is LoadState.Error || prepend is LoadState.Error) && itemCount == 0)
    }

    override fun <T : Any> progressBarVisibility(pagingItems: LazyPagingItems<T>
    ): Boolean {
        val append = pagingItems.appendState
        val refresh = pagingItems.refreshState
        return append is LoadState.Loading || refresh is LoadState.Loading
    }

    override fun <T : Any> isSwipeToRefreshEnabled(pagingItems: LazyPagingItems<T>): Boolean {
        val append = pagingItems.appendState
        val refresh = pagingItems.refreshState
        return append !is LoadState.Loading || refresh !is LoadState.Loading
    }


    private var isToastShown = false


    override fun onPaginationReachedError(append: LoadState, @StringRes errorMessage: Int) {
        if (append.endOfPaginationReached) {
            if (!isToastShown) {
                toaster.shortToast(errorMessage)
                isToastShown = true
            }
        }
    }

}