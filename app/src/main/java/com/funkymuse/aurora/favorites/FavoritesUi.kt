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
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.FavoriteBook
import com.funkymuse.aurora.components.FullSizeBoxCenteredContent
import com.funkymuse.aurora.dto.FavoriteBook
import com.funkymuse.aurora.extensions.hiltViewModel

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Composable
fun Favorites(
    navBackStackEntry: NavBackStackEntry,
    onBookClicked: (id: Int) -> Unit
) {
    val viewModel: FavoritesViewModel = hiltViewModel(navBackStackEntry)
    val favorites = viewModel.favoritesData.collectAsLazyPagingItems()
    val longClickedBook = remember { mutableStateOf<FavoriteBook?>(null) }
    if (longClickedBook.value != null) {
        DeleteBook(it = longClickedBook.value!!,
            onConfirm = { viewModel.removeFromFavorites(it) },
            onDismiss = { longClickedBook.value = null })
    }
    if (favorites.itemCount == 0) {
        FullSizeBoxCenteredContent {
            //LottieAnim(anim = R.raw.no_latest_books)
        }
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
                        onBookClicked(it.id)
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
