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
import com.funkymuse.aurora.components.FullSizeBoxCenteredContent
import com.funkymuse.aurora.components.LottieWithRetry
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
            LottieWithRetry(
                R.raw.no_latest_books,
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
                    LottieWithRetry(
                        R.raw.server_error,
                        true, onRetryClicked = {
                            viewModel.refresh()
                        })
                }
            }
        },
        apiError = { _, _ ->
            FullSizeBoxCenteredContent {
                LottieWithRetry(
                    R.raw.server_error,
                    true, onRetryClicked = {
                        viewModel.refresh()
                    })
            }
        },
        success = {
            ShowBooks(modifier = Modifier.fillMaxSize(), this) { item ->
                val bookID = item.id?.toInt() ?: return@ShowBooks
                onBookClicked(bookID, Mirrors(item.mirrors?.toList() ?: emptyList()))
            }
        }
    )

}