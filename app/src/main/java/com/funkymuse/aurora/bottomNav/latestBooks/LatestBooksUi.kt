package com.funkymuse.aurora.bottomNav.latestBooks

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.navigation.NavHostController
import com.crazylegend.kotlinextensions.internetdetector.InternetDetector
import com.crazylegend.retrofit.retrofitResult.handle
import com.crazylegend.retrofit.retrofitResult.retryWhenInternetIsAvailable
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.R
import com.funkymuse.aurora.book.Book
import com.funkymuse.aurora.dto.Book
import com.funkymuse.aurora.extensions.CardListShimmer
import com.funkymuse.aurora.ui.theme.Shapes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.navigate
import com.funkymuse.aurora.bookDetails.BOOK_DETAILS_ROUTE
import com.funkymuse.aurora.components.ShowErrorDataWithRetry

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Composable
fun LatestBooks(
    navController: NavHostController,
    @SuppressLint("RestrictedApi") viewModel: LatestBooksVM = viewModel(
        factory = HiltViewModelFactory(
            LocalContext.current,
            navController.backStack.last
        )
    ),
) {
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
            ShowEmptyData()
        },
        callError = {
            if (it is NoConnectionException) {
                NoConnectionError()
            } else {
                ShowErrorDataWithRetry(stringResource(id = R.string.no_latest_books)){
                    viewModel.refresh()
                }
            }
        },
        apiError = { _, _ ->
            ShowErrorDataWithRetry(stringResource(id = R.string.no_latest_books)){
                viewModel.refresh()
            }
        },
        success = {
            ShowBooks(this) { item ->
                viewModel.saveMirrorsForBookId(item.id, item.mirrors)
                navController.navigate("$BOOK_DETAILS_ROUTE/${item.id}") { launchSingleTop = true }
            }
        }
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ShowEmptyData() {
    Card(shape = Shapes.large,
        modifier = Modifier.padding(20.dp).wrapContentHeight()) {
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.no_books_loaded),
            style = MaterialTheme.typography.body1
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NoConnectionError() {
   Card(shape = Shapes.large,
       modifier = Modifier.padding(20.dp).wrapContentHeight()) {
           Text(
               textAlign = TextAlign.Center,
               modifier = Modifier.padding(16.dp),
               text = stringResource(id = R.string.no_books_loaded_no_connect),
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

