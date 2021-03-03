package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.bookDetails.*
import com.funkymuse.aurora.bottomNav.BottomNavScreen
import com.funkymuse.aurora.bottomNav.favorites.Favorites
import com.funkymuse.aurora.bottomNav.latestBooks.LatestBooks
import com.funkymuse.aurora.bottomNav.search.Search
import com.funkymuse.aurora.bottomNav.settings.Settings
import com.funkymuse.aurora.extensions.rememberBooleanSaveableDefaultFalse
import com.funkymuse.aurora.searchResult.SEARCH_PARAM
import com.funkymuse.aurora.searchResult.SEARCH_ROUTE_BOTTOM_NAV
import com.funkymuse.aurora.searchResult.SearchResult
import com.funkymuse.aurora.ui.theme.AuroraTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bookDetailsViewModelFactory: BookDetailsViewModel.BookDetailsVMF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroraTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AuroraScaffold(bookDetailsViewModelFactory)
                }
            }
        }
    }
}

data class BottomEntry(val screen: BottomNavScreen, val icon: ImageVector)

@Composable
fun AuroraScaffold(bookDetailsViewModelFactory: BookDetailsViewModel.BookDetailsVMF) {

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
                composable(BottomNavScreen.Search.route) {
                    Search(navController)
                }
                composable(BottomNavScreen.Favorites.route) {
                    Favorites(navController)
                }
                composable(BottomNavScreen.LatestBooks.route) {
                    LatestBooks(navController)
                }
                composable(BottomNavScreen.Settings.route) {
                    Settings(navController)
                }
                addSearchResult(navController)
                addBookDetails(navController, bookDetailsViewModelFactory)
            }
        )
    }
}

private fun NavGraphBuilder.addSearchResult(navController: NavHostController) {
    composable(
        SEARCH_ROUTE_BOTTOM_NAV,
        arguments = listOf(navArgument(SEARCH_PARAM) { type = NavType.StringType })
    ) {
        SearchResult(navController, it.arguments?.getString(SEARCH_PARAM))
    }
}

private fun NavGraphBuilder.addBookDetails(
    navController: NavHostController,
    bookDetailsViewModelFactory: BookDetailsViewModel.BookDetailsVMF
) {
    composable(
        BOOK_DETAILS_BOTTOM_NAV_ROUTE,
        arguments = listOf(navArgument(BOOK_ID_PARAM) {
            type = NavType.IntType
        })
    ) {
        it.arguments?.apply {
            ShowDetailedBook(
                getInt(BOOK_ID_PARAM),
                getStringArray(DL_MIRRORS_PARAM)?.toList(),
                navController,
                bookDetailsViewModelFactory
            )
        }
    }
}

val hideBottomNavList = listOf(BOOK_DETAILS_BOTTOM_NAV_ROUTE, SEARCH_ROUTE_BOTTOM_NAV)

@Composable
fun AuroraBottomNavigation(navController: NavHostController, bottomNavList: List<BottomEntry>) {
    var hideBottomNav by rememberBooleanSaveableDefaultFalse()
    val size = if (hideBottomNav) {
        Modifier.size(animateDpAsState(targetValue = 0.dp, animationSpec = tween()).value)
    } else {
        Modifier
    }

    BottomNavigation(modifier = size) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        debug { "CURRENT ROUTE $currentRoute" }
        hideBottomNav = currentRoute in hideBottomNavList
        bottomNavList.forEach { bottomEntry ->
            BottomNavigationItem(
                selected = currentRoute == bottomEntry.screen.route,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(bottomEntry.screen.route) {
                        popUpTo = navController.graph.startDestination
                        launchSingleTop = true
                    }
                },
                label = { Text(text = stringResource(id = bottomEntry.screen.resourceID)) },
                icon = {
                    Icon(
                        imageVector = bottomEntry.icon,
                        contentDescription = stringResource(id = bottomEntry.screen.resourceID)
                    )
                }
            )
        }
    }
}

