package com.funkymuse.aurora.latestBooks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.Book
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.components.ErrorWithRetry
import com.funkymuse.aurora.dto.Book
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.extensions.appendState
import com.funkymuse.aurora.extensions.prependState
import com.funkymuse.aurora.extensions.refreshState
import com.funkymuse.aurora.paging.PagingProviderViewModel
import com.funkymuse.composed.core.rememberBooleanDefaultFalse
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.insets.toPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LatestBooks(
    latestBooksVM: LatestBooksVM = hiltNavGraphViewModel(),
    pagingUIProvider: PagingProviderViewModel = hiltNavGraphViewModel(),
    onBookClicked: (id: Int, Mirrors) -> Unit
) {
    var progressVisibility by rememberBooleanDefaultFalse()

    val pagingItems = latestBooksVM.pagingData.collectAsLazyPagingItems()

    val scope = rememberCoroutineScope()

    progressVisibility =
        pagingUIProvider.progressBarVisibility(pagingItems.appendState, pagingItems.refreshState)
    val retry = {
        latestBooksVM.refresh()
        pagingItems.refresh()
    }
    pagingUIProvider.onPaginationReachedError(
        pagingItems.appendState,
        R.string.no_more_latest_books
    )

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (loading) = createRefs()
        AnimatedVisibility(visible = progressVisibility, modifier = Modifier
            .constrainAs(loading) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            }
            .wrapContentSize()
            .systemBarsPadding()
            .padding(top = 4.dp)
            .zIndex(2f)) {
            CircularProgressIndicator()
        }

        pagingUIProvider.OnError(
            refresh = pagingItems.refreshState,
            append = pagingItems.appendState,
            prepend = pagingItems.prependState,
            pagingItems = pagingItems,
            scope = scope,
            noInternetUI = {
                ErrorMessage(R.string.no_books_loaded_no_connect)
            },
            errorUI = {
                ErrorWithRetry(R.string.no_books_loaded) {
                    retry()
                }
            }
        )

        val swipeToRefreshState = rememberSwipeRefreshState(isRefreshing = false)
        SwipeRefresh(
            state = swipeToRefreshState, onRefresh = {
                swipeToRefreshState.isRefreshing = true
                retry()
                swipeToRefreshState.isRefreshing = false
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            ShowBooks(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
                list = pagingItems
            ) { item ->
                val bookID = item.id?.toInt() ?: return@ShowBooks
                onBookClicked(bookID, Mirrors(item.mirrors?.toList() ?: emptyList()))
            }
        }

    }

}


@Composable
fun ShowBooks(
    modifier: Modifier = Modifier,
    list: LazyPagingItems<Book>,
    onBookClicked: (Book) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues()
    ) {
        items(list) { item ->
            item ?: return@items
            Book(item) {
                onBookClicked(item)
            }
        }
    }
}