package com.funkymuse.aurora.searchResult

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
const val SEARCH_RESULT_ROUTE = "search_result"
const val SEARCH_PARAM = "query"
@Composable
fun SearchResult(navController: NavHostController, string: String?) {
    Text(text = "SEARCH QUERY $string")
}