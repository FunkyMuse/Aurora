package com.funkymuse.aurora.latestBooks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.crazylegend.kotlinextensions.internetdetector.InternetDetector
import com.crazylegend.retrofit.retrofitResult.handle
import com.crazylegend.retrofit.retrofitResult.retryWhenInternetIsAvailable
import com.crazylegend.retrofit.retryOnConnectedToInternet
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.Book
import com.funkymuse.aurora.components.FullSizeBoxCenteredContent
import com.funkymuse.aurora.components.LottieWithRetry
import com.funkymuse.aurora.dto.Book
import com.funkymuse.aurora.extensions.CardListShimmer
import com.funkymuse.aurora.extensions.hiltViewModel
import com.funkymuse.aurora.ui.theme.Shapes

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Composable
fun LatestBooks(
    navBackStackEntry: NavBackStackEntry,
    onBookClicked: (id: Int) -> Unit
) {
    val viewModel: LatestBooksVM = hiltViewModel(navBackStackEntry)
    val scope = rememberCoroutineScope()
    val internetDetector = InternetDetector(LocalContext.current)
    val list = viewModel.booksData.collectAsState()

    list.value.retryWhenInternetIsAvailable(internetDetector.state, scope) {
        viewModel.refresh()
    }

    list.value.handle(
        loading = {
            ShowLoading()
        },
        emptyData = {
            LottieWithRetry(R.raw.no_latest_books,
                true, onRetryClicked = {
                    viewModel.refresh()
                })
        },
        callError = { throwable ->
            if (throwable is NoConnectionException) {
                retryOnConnectedToInternet(
                    viewModel.internetConnection,
                    scope
                ) {
                    viewModel.refresh()
                }
                FullSizeBoxCenteredContent {
                    //LottieAnim(anim = R.raw.no_connection, size = 50.dp)
                }
            } else {
                FullSizeBoxCenteredContent {
                    LottieWithRetry(R.raw.server_error,
                        true, onRetryClicked = {
                            viewModel.refresh()
                        })
                }
            }
        },
        apiError = { _, _ ->

            FullSizeBoxCenteredContent {
                LottieWithRetry(R.raw.server_error,
                    true, onRetryClicked = {
                        viewModel.refresh()
                    })
            }
        },
        success = {
            ShowBooks(this) { item ->
                val bookID = item.id?.toInt() ?: return@ShowBooks
                viewModel.saveMirrorsForBookId(item.id, item.mirrors)
                onBookClicked(bookID)
            }
        }
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ShowEmptyData() {
    Card(
        shape = Shapes.large,
        modifier = Modifier
            .padding(20.dp)
            .wrapContentHeight()
    ) {
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.no_books_loaded),
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun ShowBooks(
    list: List<Book>,
    onBookClicked: (Book) -> Unit,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)

    ) {
        items(list, key = { it.id.toString() }) { item ->
            Book(item) {
                onBookClicked(item)
            }
        }
    }

}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowLoading() {
    CardListShimmer()
}

