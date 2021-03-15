package com.funkymuse.aurora.searchResult

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.crazylegend.kotlinextensions.intent.openWebPage
import com.crazylegend.kotlinextensions.internetdetector.InternetDetector
import com.crazylegend.retrofit.retrofitResult.handle
import com.crazylegend.retrofit.retrofitResult.retryWhenInternetIsAvailable
import com.crazylegend.retrofit.retryOnConnectedToInternet
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.R
import com.funkymuse.aurora.backButton.BackButton
import com.funkymuse.aurora.bookDetails.AddToFavorites
import com.funkymuse.aurora.bookDetails.TopAppBarBackOnly
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.components.ErrorWithRetry
import com.funkymuse.aurora.components.ScaffoldWithBackAndContent
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.extensions.assistedViewModel
import com.funkymuse.aurora.latestBooks.ShowBooks
import com.funkymuse.aurora.latestBooks.ShowLoading
import com.funkymuse.aurora.search.RadioButtonWithText
import com.funkymuse.aurora.search.RadioButtonWithTextNotClickable
import com.funkymuse.aurora.ui.theme.PrimaryVariant
import com.funkymuse.aurora.ui.theme.Shapes

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
const val SEARCH_RESULT_ROUTE = "search_result"
const val SEARCH_PARAM = "query"
const val SEARCH_IN_FIELDS_PARAM = "searchInFieldsCheckedPosition"
const val SEARCH_WITH_MASK_WORD_PARAM = "searchWithMaskWord"

const val SEARCH_ROUTE_BOTTOM_NAV =
    "$SEARCH_RESULT_ROUTE/{$SEARCH_PARAM}/{$SEARCH_IN_FIELDS_PARAM}/{$SEARCH_WITH_MASK_WORD_PARAM}"

@Composable
fun SearchResult(
    onBackClicked: () -> Unit,
    searchResultVMF: SearchResultVM.SearchResultVMF,
    searchQuery: String,
    searchInFieldsCheckedPosition: Int,
    searchWithMaskWord: Boolean,
    onBookClicked: (id: Int, mirrors: Mirrors) -> Unit

) {
    val viewModel = assistedViewModel {
        searchResultVMF.create(
            searchQuery, it,
            searchInFieldsCheckedPosition, searchWithMaskWord
        )
    }
    var checkedSortPosition by rememberSaveable { mutableStateOf(0) }

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
            ScaffoldWithBackAndContent(onBackClicked) {
                ErrorWithRetry(R.string.no_book_loaded) {
                    viewModel.refresh()
                }
            }
        },
        callError = { throwable ->
            if (throwable is NoConnectionException) {
                retryOnConnectedToInternet(
                    viewModel.internetConnection,
                    scope
                ) {
                    viewModel.refresh()
                }
                ScaffoldWithBackAndContent(onBackClicked) {
                    ErrorMessage(R.string.no_book_loaded_no_connect)
                }
            } else {
                ScaffoldWithBackAndContent(onBackClicked) {
                    ErrorWithRetry(R.string.no_book_loaded) {
                        viewModel.refresh()
                    }
                }
            }
        },
        apiError = { _, _ ->
            ScaffoldWithBackAndContent(onBackClicked) {
                ErrorWithRetry(R.string.no_book_loaded) {
                    viewModel.refresh()
                }
            }
        },
        success = {
            ScaffoldWithBackFiltersAndContent(
                checkedSortPosition,
                onBackClicked = onBackClicked,
                onSortPositionClicked = {
                    checkedSortPosition = it
                    viewModel.sortByPosition(it)
                }) {
                ShowBooks(modifier = Modifier.fillMaxSize(), this) { item ->
                    val bookID = item.id?.toInt() ?: return@ShowBooks
                    onBookClicked(bookID, Mirrors(item.mirrors?.toList() ?: emptyList()))
                }
            }
        }
    )
}

@Composable
fun ScaffoldWithBackFiltersAndContent(
    checkedSortPosition:Int,
    onBackClicked: () -> Unit,
    onSortPositionClicked: (Int) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val sortList = listOf(
        Pair(0, R.string.default_sort),
        Pair(1, R.string.year_asc),
        Pair(2, R.string.year_desc),
        Pair(3, R.string.size_asc),
        Pair(4, R.string.size_desc),
        Pair(5, R.string.author_asc),
        Pair(6, R.string.author_desc),
        Pair(7, R.string.title_asc),
        Pair(8, R.string.title_desc),
        Pair(9, R.string.extension_asc),
        Pair(10, R.string.extension_desc),
        Pair(11, R.string.publisher_asc),
        Pair(12, R.string.publisher_desc),
    )
    var dropDownMenuExpansionStatus by remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(backgroundColor = PrimaryVariant) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (backButton, filter) = createRefs()
                    BackButton(
                        modifier = Modifier
                            .constrainAs(backButton) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .padding(8.dp), onClick = onBackClicked
                    )

                    Button(
                        onClick = {
                            dropDownMenuExpansionStatus = !dropDownMenuExpansionStatus
                        },
                        shape = Shapes.large,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
                        modifier = Modifier
                            .constrainAs(filter) {
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(id = R.string.title_favorites)
                        )
                    }

                    DropdownMenu(expanded = dropDownMenuExpansionStatus,
                        modifier = Modifier.fillMaxWidth(),
                        offset = DpOffset(32.dp, 16.dp),
                        onDismissRequest = { dropDownMenuExpansionStatus = false }) {
                        sortList.forEach {
                            DropdownMenuItem(onClick = {
                                onSortPositionClicked(it.first)
                                dropDownMenuExpansionStatus = false
                            }) {
                                RadioButtonWithTextNotClickable(
                                    text = it.second,
                                    isChecked = checkedSortPosition == it.first
                                )
                            }
                        }
                    }
                }
            }
        }) {
        content(it)
    }
}