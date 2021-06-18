package com.funkymuse.aurora.latestBooks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.Book
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.components.ErrorWithRetry
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.paging.PagingUIProviderViewModel
import com.funkymuse.composed.core.lastVisibleIndex
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
    latestBooksVM: LatestBooksVM = hiltViewModel(),
    pagingUIUIProvider: PagingUIProviderViewModel = hiltViewModel(),
    onBookClicked: (id: Int, Mirrors) -> Unit
) {
    latestBooksVM.debug { "FUNCTION COMPOSED" }
    var progressVisibility by rememberBooleanDefaultFalse()
    val pagingItems = latestBooksVM.pagingData.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()
    val swipeToRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    progressVisibility =
        pagingUIUIProvider.progressBarVisibility(pagingItems.appendState, pagingItems.refreshState)
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

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (loading, backToTop) = createRefs()
        AnimatedVisibility(
            visible = progressVisibility, modifier = Modifier
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


        val lastVisibleIndexState by remember {
            derivedStateOf {
                columnState.lastVisibleIndex()
            }
        }

        val isButtonVisible = lastVisibleIndexState?.let {
            it > 20
        } ?: false

        AnimatedVisibility(visible = isButtonVisible,
            modifier = Modifier
                .constrainAs(backToTop) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }
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
                    .padding(bottom = 56.dp),
                contentPadding = listInsets
            ) {
                items(pagingItems) { item ->
                    item ?: return@items
                    Book(item) {
                        val bookID = item.id?.toInt() ?: return@Book
                        onBookClicked(bookID, Mirrors(item.mirrors?.toList() ?: emptyList()))
                    }
                }
            }
        }
    }

}

