package com.funkymuse.searchfilterdestination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.funkymuse.aurora.navigator.NavigationDestination
import com.funkymuse.composed.navigation.getRememberedResult
import com.funkymuse.composed.navigation.getResultAsState

object SearchFilterDestination : NavigationDestination {

    private const val ROUTE = "SearchFilterDestination"
     const val SEARCH_WITH_MASK_WORD = "searchWithMaskWord"
     const val SEARCH_IN_FIELDS_CHECKED_POSITION = "searchInFieldsCheckedPosition"
    private const val ARGUMENT_ROUTE =
        "$ROUTE/{$SEARCH_WITH_MASK_WORD}/{$SEARCH_IN_FIELDS_CHECKED_POSITION}"

    override fun route(): String = ARGUMENT_ROUTE

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(SEARCH_IN_FIELDS_CHECKED_POSITION) {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument(SEARCH_WITH_MASK_WORD) {
                type = NavType.BoolType
                defaultValue = false
            },
        )

    fun searchInFieldsCheckedPosition(savedStateHandle: SavedStateHandle): Int =
        savedStateHandle.get<Int>(SEARCH_IN_FIELDS_CHECKED_POSITION) ?: 0

    @Composable
    fun searchInFieldsCheckedPosition(navHostController: NavHostController): State<Int> =
        navHostController.getResultAsState(SEARCH_IN_FIELDS_CHECKED_POSITION, 0)

    fun setSearchInFieldsCheckedPosition(navHostController: NavHostController, value: Int) {
        navHostController.previousBackStackEntry?.savedStateHandle?.set(
            SEARCH_IN_FIELDS_CHECKED_POSITION,
            value
        )
    }

    fun searchWithMaskWord(savedStateHandle: SavedStateHandle): Boolean =
        savedStateHandle.get<Boolean>(SEARCH_WITH_MASK_WORD) ?: false

    @Composable
    fun searchWithMaskWord(hostController: NavHostController): State<Boolean> =
        hostController.getResultAsState(SEARCH_WITH_MASK_WORD, false)

    fun setSearchWithMaskWord(navHostController: NavHostController, value: Boolean) {
        navHostController.previousBackStackEntry?.savedStateHandle?.set(
            SEARCH_WITH_MASK_WORD,
            value
        )
    }


    fun createRoute(searchInFieldsCheckedPosition: Int, searchWithMaskWord: Boolean): String =
        "$ROUTE/$searchWithMaskWord/$searchInFieldsCheckedPosition"
}