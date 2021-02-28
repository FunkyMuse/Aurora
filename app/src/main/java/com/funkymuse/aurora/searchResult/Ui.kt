package com.funkymuse.aurora.searchResult

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.funkymuse.aurora.bottomNav.search.SearchViewModel

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
const val SEARCH_RESULT_ROUTE = "search_result"
const val SEARCH_PARAM = "query"

const val SEARCH_ROUTE_BOTTOM_NAV = "$SEARCH_RESULT_ROUTE/{$SEARCH_PARAM}"
@Composable
fun SearchResult(navController: NavHostController, string: String?) {
    Text(text = "SEARCH QUERY $string")

}