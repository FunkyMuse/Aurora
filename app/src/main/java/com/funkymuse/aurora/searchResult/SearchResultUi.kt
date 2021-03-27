package com.funkymuse.aurora.searchResult

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import com.crazylegend.retrofit.retrofitResult.RetrofitResult
import com.crazylegend.retrofit.retrofitResult.handle
import com.crazylegend.retrofit.retrofitResult.retryWhenInternetIsAvailable
import com.crazylegend.retrofit.retryOnConnectedToInternet
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.R
import com.funkymuse.aurora.components.BackButton
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.components.ErrorWithRetry
import com.funkymuse.aurora.components.ScaffoldWithBackAndContent
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.extensions.assistedViewModel
import com.funkymuse.aurora.extensions.stateWhenStarted
import com.funkymuse.aurora.internetDetector.InternetDetectorViewModel
import com.funkymuse.aurora.latestBooks.ShowBooksSearch
import com.funkymuse.aurora.latestBooks.ShowLoading
import com.funkymuse.aurora.search.RadioButtonEntries
import com.funkymuse.aurora.search.RadioButtonWithText
import com.funkymuse.aurora.search.RadioButtonWithTextNotClickable
import com.funkymuse.aurora.ui.theme.BottomSheetShapes
import com.funkymuse.aurora.ui.theme.PrimaryVariant
import com.funkymuse.aurora.ui.theme.Shapes
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

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
    searchResultVMF: SearchResultViewModel.SearchResultVMF,
    searchQuery: String,
    searchInFieldsCheckedPositionParam: Int,
    searchWithMaskWordParam: Boolean,
    onBookClicked: (id: Int, mirrors: Mirrors) -> Unit

) {
    val searchResultViewModel = assistedViewModel {
        searchResultVMF.create(
            searchQuery, it,
            searchInFieldsCheckedPositionParam, searchWithMaskWordParam
        )
    }
    val internetDetectorViewModel = hiltNavGraphViewModel<InternetDetectorViewModel>()
    var checkedSortPosition by rememberSaveable { mutableStateOf(0) }

    var searchInFieldsCheckedPosition by rememberSaveable {
        mutableStateOf(
            searchInFieldsCheckedPositionParam
        )
    }
    var searchWithMaskWord by rememberSaveable { mutableStateOf(searchWithMaskWordParam) }

    val scope = rememberCoroutineScope()

    val list by stateWhenStarted(
        flow = searchResultViewModel.booksData,
        initial = RetrofitResult.Loading
    )

    list.retryWhenInternetIsAvailable(internetDetectorViewModel.internetConnection, scope) {
        searchResultViewModel.refresh()
    }

    list.handle(
        loading = {
            ShowLoading()
        },
        emptyData = {
            ScaffoldWithBackAndContent(onBackClicked) {
                ErrorWithRetry(R.string.no_book_loaded) {
                    searchResultViewModel.refresh()
                }
            }
        },
        callError = { throwable ->
            if (throwable is NoConnectionException) {
                retryOnConnectedToInternet(
                    internetDetectorViewModel.internetConnection,
                    scope
                ) {
                    searchResultViewModel.refresh()
                }
                ScaffoldWithBackAndContent(onBackClicked) {
                    ErrorMessage(R.string.no_book_loaded_no_connect)
                }
            } else {
                ScaffoldWithBackAndContent(onBackClicked) {
                    ErrorWithRetry(R.string.no_book_loaded) {
                        searchResultViewModel.refresh()
                    }
                }
            }
        },
        apiError = { _, _ ->
            ScaffoldWithBackAndContent(onBackClicked) {
                ErrorWithRetry(R.string.no_book_loaded) {
                    searchResultViewModel.refresh()
                }
            }
        },
        success = {
            ScaffoldWithBackFiltersAndContent(
                checkedSortPosition,
                searchInFieldsCheckedPosition,
                searchWithMaskWord,
                onBackClicked = onBackClicked,
                onSortPositionClicked = {
                    checkedSortPosition = it
                    searchResultViewModel.sortByPosition(it)
                },
                onSearchInFieldsCheckedPosition = {
                    searchInFieldsCheckedPosition = it
                    searchResultViewModel.searchInFieldsByPosition(it)
                },
                onSearchWithMaskWord = {
                    searchWithMaskWord = it
                    searchResultViewModel.searchWithMaskedWord(it)
                }) {
                ShowBooksSearch(this) { item ->
                    val bookID = item.id?.toInt() ?: return@ShowBooksSearch
                    onBookClicked(bookID, Mirrors(item.mirrors?.toList() ?: emptyList()))
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScaffoldWithBackFiltersAndContent(
    checkedSortPosition: Int,
    searchInFieldsCheckedPosition: Int,
    searchWithMaskWord: Boolean,
    onBackClicked: () -> Unit,
    onSortPositionClicked: (Int) -> Unit,
    onSearchInFieldsCheckedPosition: (Int) -> Unit,
    onSearchWithMaskWord: (Boolean) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    val state = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = state)
    val scope = rememberCoroutineScope()


    val searchInFieldEntries = listOf(
        RadioButtonEntries(R.string.default_column),
        RadioButtonEntries(R.string.title),
        RadioButtonEntries(R.string.author),
        RadioButtonEntries(R.string.series),
        RadioButtonEntries(R.string.publisher),
        RadioButtonEntries(R.string.year),
        RadioButtonEntries(R.string.isbn),
        RadioButtonEntries(R.string.language),
        RadioButtonEntries(R.string.md5),
        RadioButtonEntries(R.string.tags),
        RadioButtonEntries(R.string.extension),
    )

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
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    BottomSheetScaffold(sheetContent = {
        LazyColumn {

            item {
                Text(
                    text = stringResource(R.string.search_in_fields), modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                )
            }

            itemsIndexed(searchInFieldEntries) { index, item ->
                RadioButtonWithText(
                    text = item.title,
                    isChecked = searchInFieldsCheckedPosition == index,
                    onRadioButtonClicked = {
                        onSearchInFieldsCheckedPosition(index)

                        scope.launch { state.collapse() }
                    })
            }

            item {
                Text(
                    text = stringResource(R.string.mask_word), modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                )
            }

            item {
                RadioButtonWithText(
                    text = R.string.search_with_mask_word,
                    isChecked = searchWithMaskWord,
                    onRadioButtonClicked = {
                        onSearchWithMaskWord(!searchWithMaskWord)
                    })
            }

            item {
                Spacer(modifier = Modifier.padding(bottom = 64.dp))
            }
        }
    },
        sheetPeekHeight = 0.dp,
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetShape = BottomSheetShapes.large,
        topBar = {
            TopAppBar(backgroundColor = PrimaryVariant, modifier = Modifier.statusBarsPadding()) {
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
                            dropDownMenuExpanded = !dropDownMenuExpanded
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

                    DropdownMenu(expanded = dropDownMenuExpanded,
                        modifier = Modifier.fillMaxWidth(),
                        offset = DpOffset(32.dp, 16.dp),
                        onDismissRequest = { dropDownMenuExpanded = false }) {
                        sortList.forEach {
                            DropdownMenuItem(onClick = {
                                onSortPositionClicked(it.first)
                                dropDownMenuExpanded = false
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

        ConstraintLayout {
            val filter = createRef()

            Box(modifier = Modifier.fillMaxSize()) {
                content(it)
            }

            Box(
                modifier = Modifier
                    .constrainAs(filter) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(bottom = 12.dp)
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .navigationBarsPadding(),
                    onClick = { scope.launch { state.expand() } },
                ) {
                    Icon(
                        Icons.Filled.FilterList,
                        contentDescription = stringResource(id = R.string.filter),
                        tint = Color.White
                    )
                }
            }
        }

    }
}