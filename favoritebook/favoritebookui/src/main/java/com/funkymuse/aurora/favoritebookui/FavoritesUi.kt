package com.funkymuse.aurora.favoritebookui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bookui.Book
import com.funkymuse.aurora.confirmationdialog.ConfirmationDialog
import com.funkymuse.aurora.errorcomponent.ErrorMessage
import com.funkymuse.aurora.favoritebookdb.FavoritesViewModel
import com.funkymuse.aurora.favoritebookmodel.FavoriteBook
import com.funkymuse.aurora.paging.PagingUIProviderViewModel
import com.funkymuse.aurora.paging.appendState
import com.funkymuse.aurora.toaster.ToasterViewModel
import com.funkymuse.composed.core.rememberBooleanDefaultFalse
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.conflate

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */


@OptIn(ExperimentalAnimationApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun Favorites() {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val pagingUIProviderViewModel: PagingUIProviderViewModel = hiltViewModel()
    var progressVisibility by rememberBooleanDefaultFalse()
    val favorites = viewModel.favoritesData.collectAsLazyPagingItems()
    val longClickedBook = remember { mutableStateOf<FavoriteBook?>(null) }
    val toaster: ToasterViewModel = hiltViewModel()

    longClickedBook.value?.apply {
        DeleteBook(it = this,
                onConfirm = { viewModel.removeFromFavorites(id) },
                onDismiss = { longClickedBook.value = null })
    }


    //gotta make this workaround bcuz the paging library itemCount always starts with 0 :(
    val isDatabaseEmpty by viewModel.count.conflate()
            .collectAsStateWithLifecycle(false)

    progressVisibility =
            pagingUIProviderViewModel.progressBarVisibility(favorites)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = progressVisibility, modifier = Modifier
            .align(Alignment.TopCenter)
            .wrapContentSize()
            .systemBarsPadding()
            .padding(top = 4.dp)
            .zIndex(2f)) {
            CircularProgressIndicator()
        }

        if (isDatabaseEmpty && !progressVisibility) {
            ErrorMessage(text = R.string.no_favorites_expl)
        } else {
            pagingUIProviderViewModel.onPaginationReachedError(
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
                modifier = Modifier.fillMaxSize()
        ) {

            LazyColumn(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 56.dp, top = 8.dp),
                    contentPadding = WindowInsets.systemBars.asPaddingValues()
            ) {

                items(favorites) { book ->
                    book?.let { favoriteBook ->
                        Book(favoriteBook,
                                onCopiedToClipBoard = { toaster.shortToast(it) },
                                onLongClick = { longClickedBook.value = favoriteBook }) {
                            viewModel.navigate(BookDetailsDestination.createBookDetailsRoute(favoriteBook.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteBook(
        it: FavoriteBook = FavoriteBook(),
        onDismiss: () -> Unit = {}, onConfirm: (id: String) -> Unit = {}
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

