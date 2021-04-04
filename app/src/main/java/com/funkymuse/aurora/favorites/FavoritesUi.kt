package com.funkymuse.aurora.favorites

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.Book
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.dto.FavoriteBook
import com.funkymuse.aurora.dto.Mirrors
import com.google.accompanist.insets.systemBarsPadding

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */


@Composable
fun Favorites(
    navBackStackEntry: NavBackStackEntry,
    onBookClicked: (id: Int, mirrors: Mirrors) -> Unit
) {
    val viewModel: FavoritesViewModel = hiltNavGraphViewModel(navBackStackEntry)
    val favorites = viewModel.favoritesData.collectAsLazyPagingItems()
    val longClickedBook = remember { mutableStateOf<FavoriteBook?>(null) }
    longClickedBook.value?.apply {
        DeleteBook(it = this,
            onConfirm = { viewModel.removeFromFavorites(it) },
            onDismiss = { longClickedBook.value = null })
    }

    favorites.loadState.source

    if (favorites.itemCount == 0) {
        ErrorMessage(text = R.string.no_favorites_expl)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
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
            }, modifier = Modifier.padding(horizontal = 4.dp)) {
                Text(text = stringResource(id = R.string.remove))
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }, modifier = Modifier.padding(horizontal = 4.dp)) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}
