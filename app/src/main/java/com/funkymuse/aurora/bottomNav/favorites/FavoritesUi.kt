package com.funkymuse.aurora.bottomNav.favorites

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.FavoriteBook
import com.funkymuse.aurora.bookDetails.BOOK_DETAILS_ROUTE
import com.funkymuse.aurora.bottomNav.latestBooks.ShowEmptyData
import com.funkymuse.aurora.dto.FavoriteBook

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Composable
fun Favorites(
    navController: NavHostController,
    @SuppressLint("RestrictedApi") viewModel: FavoritesViewModel = viewModel(
        factory =
        HiltViewModelFactory(LocalContext.current, navController.backStack.last)
    )
) {
    val favorites = viewModel.favoritesData.collectAsLazyPagingItems()
    val longClickedBook = remember { mutableStateOf<FavoriteBook?>(null) }

    if (longClickedBook.value != null) {
        DeleteBook(it = longClickedBook.value!!,
            onConfirm = { viewModel.removeFromFavorites(it) },
            onDismiss = { longClickedBook.value = null })
    }
    if (favorites.itemCount == 0){
        ShowEmptyData()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            items(favorites) { book ->
                book?.let {
                    FavoriteBook(it, onLongClick = {
                        longClickedBook.value = it
                    }) {
                        viewModel.saveMirrorsForBookId(it.id, it.mirrors)
                        navController.navigate("$BOOK_DETAILS_ROUTE/${it.id}") {
                            launchSingleTop = true
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
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.remove_book_from_favs, it.title.toString()))
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(it.id)
                onDismiss()
            }) {
                Text(text = stringResource(id = R.string.remove))
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}
