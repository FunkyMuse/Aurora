package com.funkymuse.aurora

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.bottomNav.BottomNavScreen
import com.funkymuse.aurora.bottomNav.favorites.Favorites
import com.funkymuse.aurora.bottomNav.latestBooks.LatestBooks
import com.funkymuse.aurora.bottomNav.search.Search
import com.funkymuse.aurora.bottomNav.settings.Settings
import com.funkymuse.aurora.searchResult.SEARCH_PARAM
import com.funkymuse.aurora.searchResult.SEARCH_RESULT_ROUTE
import com.funkymuse.aurora.searchResult.SearchResult
import com.funkymuse.aurora.ui.theme.AuroraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroraTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    BottomNav()
                }
            }
        }
    }
}

data class BottomEntry(val screen: BottomNavScreen, val icon: ImageVector)

@Preview(showSystemUi = true)
@Composable
fun BottomNav() {
    val navController = rememberNavController()
    val bottomNavList =
        listOf(
            BottomEntry(
                BottomNavScreen.Search,
                Icons.Filled.Search
            ),
            BottomEntry(
                BottomNavScreen.Favorites,
                Icons.Filled.Favorite
            ),
            BottomEntry(
                BottomNavScreen.LatestBooks,
                Icons.Filled.List
            ),
            BottomEntry(
                BottomNavScreen.Settings,
                Icons.Filled.Settings
            ),
        )

    Scaffold(
        bottomBar = {
            AuroraBottomNavigation(navController, bottomNavList)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Search.route,
            builder = {
                bottomNavList.forEach { entry ->
                    addBottomNavDestinations(navController, entry)
                }
                addSearchResult(navController)
            }
        )
    }
}

private fun NavGraphBuilder.addBottomNavDestinations(
    navController: NavHostController,
    entry: BottomEntry
) {
    composable(entry.screen.route) {
        when (entry.screen) {
            BottomNavScreen.Favorites -> Favorites(navController)
            BottomNavScreen.LatestBooks -> LatestBooks(navController)
            BottomNavScreen.Search -> Search(navController)
            BottomNavScreen.Settings -> Settings(navController)
        }
    }
}

private fun NavGraphBuilder.addSearchResult(navController: NavHostController) {
    composable(
        "$SEARCH_RESULT_ROUTE/{$SEARCH_PARAM}",
        arguments = listOf(navArgument(SEARCH_PARAM) { type = NavType.StringType })
    ) {
        SearchResult(navController, it.arguments?.getString(SEARCH_PARAM))
    }
}

@Composable
fun AuroraBottomNavigation(navController: NavHostController, bottomNavList: List<BottomEntry>) {
    BottomNavigation() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        bottomNavList.forEach { bottomEntry ->
            BottomNavigationItem(
                selected = currentRoute == bottomEntry.screen.route,
                onClick = {
                    navController.navigate(bottomEntry.screen.route) {
                        popUpTo = navController.graph.startDestination
                        launchSingleTop = true
                    }
                },
                label = { Text(text = stringResource(id = bottomEntry.screen.resourceID)) },
                icon = { Icon(imageVector = bottomEntry.icon, contentDescription = stringResource(id = bottomEntry.screen.resourceID)) }
            )
        }
    }
}
