package com.funkymuse.aurora.searchresultdestination

import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.funkymuse.aurora.navigator.NavigationDestination

/**
 * Created by funkymuse on 6/26/21 to long live and prosper !
 */
object SearchResultDestination : NavigationDestination {

    private const val SEARCH_RESULT_ROUTE = "search_result"

    const val SEARCH_PARAM = "query"
    const val SEARCH_IN_FIELDS_PARAM = "searchInFieldsCheckedPosition"
    const val SEARCH_WITH_MASK_WORD_PARAM = "searchWithMaskWord"

    private const val SEARCH_ROUTE_BOTTOM_NAV =
        "$SEARCH_RESULT_ROUTE/{$SEARCH_PARAM}/{$SEARCH_IN_FIELDS_PARAM}/{$SEARCH_WITH_MASK_WORD_PARAM}"

    fun createSearchRoute(
        inputText: String,
        searchInFieldsCheckedPosition: Int,
        searchWithMaskWord: Boolean
    ): String = "$SEARCH_RESULT_ROUTE/$inputText/$searchInFieldsCheckedPosition/$searchWithMaskWord"

    override fun route(): String = SEARCH_ROUTE_BOTTOM_NAV

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(SEARCH_PARAM) { type = NavType.StringType },
            navArgument(SEARCH_IN_FIELDS_PARAM) {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument(SEARCH_WITH_MASK_WORD_PARAM) {
                type = NavType.BoolType
                defaultValue = false
            }
        )
}