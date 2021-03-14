package com.funkymuse.aurora.searchResult

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.crazylegend.kotlinextensions.internetdetector.InternetDetector
import com.crazylegend.retrofit.retrofitResult.handle
import com.crazylegend.retrofit.retrofitResult.retryWhenInternetIsAvailable
import com.crazylegend.retrofit.retryOnConnectedToInternet
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.R
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.components.ErrorWithRetry
import com.funkymuse.aurora.components.ScaffoldWithBackAndContent
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.extensions.assistedViewModel
import com.funkymuse.aurora.latestBooks.ShowBooks
import com.funkymuse.aurora.latestBooks.ShowLoading

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
const val SEARCH_RESULT_ROUTE = "search_result"
const val SEARCH_PARAM = "query"
const val SEARCH_IN_PARAM = "searchInCheckedPosition"
const val SEARCH_IN_FIELDS_PARAM = "searchInFieldsCheckedPosition"
const val SEARCH_WITH_MASK_WORD_PARAM = "searchWithMaskWord"

const val SEARCH_ROUTE_BOTTOM_NAV =
    "$SEARCH_RESULT_ROUTE/{$SEARCH_PARAM}/{$SEARCH_IN_PARAM}/{$SEARCH_IN_FIELDS_PARAM}/{$SEARCH_WITH_MASK_WORD_PARAM}"

@Composable
fun SearchResult(
    onBackClicked: () -> Unit,
    searchResultVMF: SearchResultVM.SearchResultVMF,
    searchQuery: String,
    searchInCheckedPosition: Int,
    searchInFieldsCheckedPosition: Int,
    searchWithMaskWord: Boolean,
    onBookClicked: (id: Int, mirrors: Mirrors) -> Unit

) {
    val viewModel = assistedViewModel {
        searchResultVMF.create(searchQuery)
    }

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
            ScaffoldWithBackAndContent(onBackClicked = onBackClicked) {
                ShowBooks(modifier = Modifier.fillMaxSize(), this) { item ->
                    val bookID = item.id?.toInt() ?: return@ShowBooks
                    onBookClicked(bookID, Mirrors(item.mirrors?.toList() ?: emptyList()))
                }
            }
        }
    )

}