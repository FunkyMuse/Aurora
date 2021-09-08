package com.funkymuse.aurora.latestbooksui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bookui.Book
import com.funkymuse.aurora.errorcomponent.ErrorMessage
import com.funkymuse.aurora.errorcomponent.ErrorWithRetry
import com.funkymuse.aurora.latestbooksdata.LatestBooksVM
import com.funkymuse.aurora.paging.PagingUIProviderViewModel
import com.funkymuse.aurora.paging.appendState
import com.funkymuse.aurora.toaster.ToasterViewModel
import com.funkymuse.composed.core.lazylist.lastVisibleIndexState
import com.funkymuse.composed.core.rememberBooleanDefaultFalse
import com.google.accompanist.insets.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LatestBooks(
) {
    val latestBooksVM: LatestBooksVM = hiltViewModel()
    val pagingUIUIProvider: PagingUIProviderViewModel = hiltViewModel()
    val toaster: ToasterViewModel = hiltViewModel()
    var progressVisibility by rememberBooleanDefaultFalse()
    val pagingItems = latestBooksVM.pagingData.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()
    val swipeToRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    progressVisibility =
            pagingUIUIProvider.progressBarVisibility(pagingItems)
    val retry = {
        latestBooksVM.refresh()
        pagingItems.refresh()
    }

    if (!pagingUIUIProvider.isDataEmpty(pagingItems)) {
        pagingUIUIProvider.onPaginationReachedError(
                pagingItems.appendState,
                R.string.no_more_latest_books
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
                visible = progressVisibility, modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentSize()
                .systemBarsPadding()
                .padding(top = 8.dp)
                .zIndex(2f)) {
            CircularProgressIndicator()
        }


        val lastVisibleIndexState by columnState.lastVisibleIndexState()

        val isButtonVisible = lastVisibleIndexState?.let {
            it > 20
        } ?: false

        AnimatedVisibility(visible = isButtonVisible,
                modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(start = false, end = false)
                        .padding(bottom = 64.dp)
                        .zIndex(2f)) {

            Box {
                FloatingActionButton(
                        modifier = Modifier.padding(5.dp),
                        onClick = { scope.launch { columnState.scrollToItem(0) } },
                ) {
                    Icon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = stringResource(id = R.string.go_back_to_top),
                            tint = Color.White
                    )
                }
            }
        }

        pagingUIUIProvider.OnError(
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


        val listInsets = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.systemBars)

        SwipeRefresh(
                state = swipeToRefreshState, onRefresh = {
            swipeToRefreshState.isRefreshing = true
            retry()
            swipeToRefreshState.isRefreshing = false
        },
                modifier = Modifier
                        .fillMaxSize()
        ) {

            LazyColumn(
                    state = columnState,
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 56.dp, top = 8.dp),
                    contentPadding = listInsets
            ) {
                items(pagingItems, key = { it.id }) { item ->
                    item ?: return@items
                    Book(item, onCopiedToClipBoard = { toaster.shortToast(it) }) {
                        val bookID = item.id.lowercase()
                        latestBooksVM.navigate(BookDetailsDestination.createBookDetailsRoute(bookID))
                    }
                }
            }
        }
    }

}

