package com.funkymuse.aurora.paging.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.crazylegend.internetdetector.InternetDetector
import com.crazylegend.retrofit.retryOnConnectedToInternet
import com.crazylegend.toaster.Toaster
import com.funkymuse.aurora.R
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
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

    override fun isDataEmptyWithError(
        append: LoadState,
        refresh: LoadState,
        itemCount: Int
    ): Boolean = ((append is LoadState.Error || refresh is LoadState.Error) && itemCount == 0)

    override fun isDataEmptyWithError(
        refresh: LoadState,
        append: LoadState,
        prepend: LoadState,
        itemCount: Int
    ): Boolean =
        ((append is LoadState.Error || refresh is LoadState.Error || prepend is LoadState.Error) && itemCount == 0)


    private var isToastShown = false

    @Composable
    override fun <T : Any> OnError(
        refresh: LoadState,
        append: LoadState,
        prepend: LoadState,
        scope: CoroutineScope,
        pagingItems: LazyPagingItems<T>,
        noInternetUI: @Composable () -> Unit,
        errorUI: @Composable () -> Unit
    ) {
        if (isLoadStateNoConnectionException(refresh) ||
            isLoadStateNoConnectionException(append) ||
            isLoadStateNoConnectionException(prepend)
        ) {

            if (pagingItems.itemCount == 0) {
                noInternetUI()
            } else {
                toaster.longToast(R.string.no_connection_message_will_load_on_connection_achieved)
            }

            retryOnConnectedToInternet(internetDetector.state, scope) {
                pagingItems.retry()
            }

        } else {
            if (isDataEmptyWithError(append, refresh, prepend, pagingItems.itemCount)) {
                errorUI()
            }
        }
    }


    override fun onPaginationReachedError(append: LoadState, @StringRes errorMessage: Int) {
        if (append.endOfPaginationReached) {
            if (!isToastShown) {
                toaster.shortToast(errorMessage)
                isToastShown = true
            }
        }
    }

    override fun progressBarVisibility(
        append: LoadState,
        refresh: LoadState
    ) = append is LoadState.Loading || refresh is LoadState.Loading


    override fun isSwipeToRefreshEnabled(append: LoadState, refresh: LoadState): Boolean =
        append !is LoadState.Loading || refresh !is LoadState.Loading

}