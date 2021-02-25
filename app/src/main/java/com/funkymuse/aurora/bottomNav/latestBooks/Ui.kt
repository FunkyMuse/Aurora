package com.funkymuse.aurora.bottomNav.latestBooks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.crazylegend.kotlinextensions.log.debug
import com.crazylegend.retrofit.retrofitResult.handle
import com.funkymuse.aurora.R
import com.funkymuse.aurora.bottomNav.book.Book
import com.funkymuse.aurora.dto.Book
import com.funkymuse.aurora.ui.theme.Shapes

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Composable
fun LatestBooks(navController: NavHostController) {

    val viewModel = viewModel<LatestBooksVM>(
        factory = HiltViewModelFactory(LocalContext.current, navController.backStack.last)
    )

    val list = viewModel.booksData.collectAsState()

    list.value.handle(
        loading = {
            ShowLoading()
        },
        emptyData = {

        },
        callError = {
            it.printStackTrace()
            ShowServerErrorData()
        },
        apiError = { errorBody, responseCode ->
            list.debug { "ERROR $errorBody $responseCode" }
            ShowServerErrorData()
        },
        success = {
            ShowBooks(this, navController)
        }
    )
}

@Composable
fun ShowBooks(list: List<Book>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        items(list) { item ->
            Book(item){

            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowServerErrorData() {
    AnimatedVisibility(visible = true) {
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
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
