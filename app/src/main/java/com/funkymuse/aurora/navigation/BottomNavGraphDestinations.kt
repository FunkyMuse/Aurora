package com.funkymuse.aurora.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.funkymuse.aurora.bottomnavigation.*
import com.funkymuse.aurora.favoritebookui.Favorites
import com.funkymuse.aurora.latestbooksui.LatestBooks
import com.funkymuse.aurora.searchui.Search
import com.funkymuse.aurora.settingsui.Settings

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */


@OptIn(ExperimentalMaterialApi::class)
private val destinationsBottomNav: Map<BottomNavigationEntry, @Composable () -> Unit> = mapOf(
    SearchRoute to { Search() },
    FavoritesRoute to { Favorites() },
    LatestBooksRoute to { LatestBooks() },
    SettingsRoute to { Settings() },
)


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addBottomNavigationDestinations(navController: NavHostController) {
    destinationsBottomNav.forEach { entry ->
        val destination = entry.key
        composable(destination.route) {
            entry.value()
        }
    }
}