package com.funkymuse.aurora.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.funkymuse.searchfilterdestination.SearchFilterDestination


@OptIn(ExperimentalMaterialApi::class)
private val destinationsBottomNav: Map<BottomNavigationEntry, @Composable (NavHostController) -> Unit> =
    mapOf(
        SearchRoute to {
            Search()
        },
        FavoritesRoute to { Favorites() },
        LatestBooksRoute to { LatestBooks() },
        SettingsRoute to { Settings() },
    )


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addBottomNavigationDestinations(navController: NavHostController) {
    destinationsBottomNav.forEach { entry ->
        val destination = entry.key
        composable(destination.route) {
            entry.value(navController)
        }
    }
}