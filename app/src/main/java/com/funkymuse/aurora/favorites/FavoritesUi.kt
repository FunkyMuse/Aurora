package com.funkymuse.aurora.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.Book
import com.funkymuse.aurora.components.ConfirmationDialog
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.dto.FavoriteBook
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.extensions.appendState
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
fun Favorites(
    viewModel: FavoritesViewModel = hiltViewModel(),
    pagingProviderViewModel: PagingProviderViewModel = hiltViewModel(),
    onBookClicked: (id: Int, mirrors: Mirrors) -> Unit
) {
    var progressVisibility by rememberBooleanDefaultFalse()
    val favorites = viewModel.favoritesData.collectAsLazyPagingItems()
    val longClickedBook = remember { mutableStateOf<FavoriteBook?>(null) }
    longClickedBook.value?.apply {
        DeleteBook(it = this,
            onConfirm = { viewModel.removeFromFavorites(it) },
            onDismiss = { longClickedBook.value = null })
    }

    progressVisibility =
        pagingProviderViewModel.progressBarVisibility(favorites.appendState, favorites.refreshState)

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

        if (pagingProviderViewModel.isDataEmpty(favorites)) {
            ErrorMessage(text = R.string.no_favorites_expl)
        } else {
            pagingProviderViewModel.onPaginationReachedError(
                favorites.appendState,
                R.string.no_more_favorite_books
            )
        }

        val swipeToRefreshState = rememberSwipeRefreshState(isRefreshing = false)
        SwipeRefresh(
            state = swipeToRefreshState, onRefresh = {
                swipeToRefreshState.isRefreshing = true
                favorites.refresh()
                swipeToRefreshState.isRefreshing = false
            },
            modifier = Modifier
                .fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                contentPadding = LocalWindowInsets.current.navigationBars.toPaddingValues(
                    top = false,
                    additionalBottom = 16.dp
                )
            ) {

                items(favorites) { book ->
                    book?.let {
                        Book(it, onLongClick = {
                            longClickedBook.value = it
                        }) {
                            onBookClicked(it.id, Mirrors(it.mirrors ?: emptyList()))
                        }
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun DeleteBook(
    it: FavoriteBook = FavoriteBook(
        title = "My favorite book"
    ), onDismiss: () -> Unit = {}, onConfirm: (id: Int) -> Unit = {}
) {
    ConfirmationDialog(
        title = stringResource(
            R.string.remove_book_from_favs,
            it.title.toString()
        ), onDismiss = onDismiss, onConfirm = {
            onConfirm(it.id)
        }, confirmText = stringResource(id = R.string.remove)
    )
}

