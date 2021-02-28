package com.funkymuse.aurora.bottomNav.latestBooks

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
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
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.bookDetails.BOOK_DETAILS_ROUTE

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
                ShowServerErrorData()
            }
        },
        apiError = { _, _ ->
            ShowServerErrorData()
        },
        success = {
            ShowBooks(this, navController)
        }
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ShowEmptyData() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
    navController: NavHostController,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)

    ) {
        items(list) { item ->
            Book(item) {
                navController.navigate("$BOOK_DETAILS_ROUTE/${item.id}") { launchSingleTop = true }
            }
        }
    }

}


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowServerErrorData() {
    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
        Card(
            shape = Shapes.large,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.no_latest_books),
                modifier = Modifier
                    .padding(16.dp),
                fontSize = 24.sp
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowLoading() {
    CardListShimmer()
}

