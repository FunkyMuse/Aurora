package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.bookDetails.*
import com.funkymuse.aurora.bottomnavigation.BottomEntry
import com.funkymuse.aurora.bottomnavigation.BottomNav
import com.funkymuse.aurora.bottomnavigation.destinations.FavoritesBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.LatestBooksBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SearchBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SettingsBottomNavRoute
import com.funkymuse.aurora.favorites.Favorites
import com.funkymuse.aurora.latestBooks.LatestBooks
import com.funkymuse.aurora.search.Search
import com.funkymuse.aurora.searchResult.SEARCH_ROUTE_BOTTOM_NAV
import com.funkymuse.aurora.searchResult.SearchResult
import com.funkymuse.aurora.searchResult.createSearchRoute
import com.funkymuse.aurora.searchResult.searchResultArguments
import com.funkymuse.aurora.settings.Settings
import com.funkymuse.aurora.settings.SettingsViewModel
import com.funkymuse.composed.core.rememberBooleanSaveableDefaultFalse
import com.funkymuse.style.shape.BottomSheetShapes
import com.funkymuse.style.theme.AuroraTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AuroraTheme(darkThemeFlow = hiltViewModel<SettingsViewModel>().darkTheme) {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Surface(color = MaterialTheme.colors.background) {
                        AuroraScaffold()
                    }
                }
            }
        }
    }
}

@Composable
fun AuroraScaffold() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AuroraBottomNavigation(navController, BottomNav.bottomNavigationEntries)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = SearchBottomNavRoute.route,
            builder = {
                composable(SearchBottomNavRoute.route) {
                    Search { inputText, searchInFieldsCheckedPosition, searchWithMaskWord ->
                        openSearchResult(
                            navController,
                            inputText.trim(),
                            searchInFieldsCheckedPosition,
                            searchWithMaskWord
                        )
                    }
                }
                composable(FavoritesBottomNavRoute.route) {
                    Favorites { id, mirrors ->
                        it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
                        openDetailedBook(navController, id)
                    }
                }
                composable(LatestBooksBottomNavRoute.route) {
                    LatestBooks() { id, mirrors ->
                        it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
                        openDetailedBook(navController, id)
                    }
                }
                composable(SettingsBottomNavRoute.route) {
                    Settings()
                }
                addSearchResult(navController)
                addBookDetails(navController)
            }
        )
    }
}

fun openSearchResult(
    navController: NavHostController,
    inputText: String,
    searchInFieldsCheckedPosition: Int,
    searchWithMaskWord: Boolean
) {

    navController.navigate(
        createSearchRoute(
            inputText,
            searchInFieldsCheckedPosition,
            searchWithMaskWord
        )
    ) {
        launchSingleTop = true
    }
}

fun openDetailedBook(navController: NavHostController, id: Int) {
    navController.navigate("$BOOK_DETAILS_ROUTE/${id}") {
        launchSingleTop = true
    }
}

private fun NavGraphBuilder.addSearchResult(
    navController: NavHostController,
) {
    composable(
        SEARCH_ROUTE_BOTTOM_NAV,
        searchResultArguments
    ) {
        SearchResult(onBackClicked = {
            navController.navigateUp()
        }) { id: Int, mirrors ->
            it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
            openDetailedBook(navController, id)
        }
    }
}

private fun NavGraphBuilder.addBookDetails(
    navController: NavHostController,
) {
    composable(
        BOOK_DETAILS_BOTTOM_NAV_ROUTE,
        arguments = listOf(
            navArgument(BOOK_ID_PARAM) {
                type = NavType.IntType
            },
        )
    ) {
        it.arguments?.apply {
            ShowDetailedBook(
                getInt(BOOK_ID_PARAM),
                navController.previousBackStackEntry?.arguments?.getParcelable(BOOK_MIRRORS_PARAM),
            ) {
                navController.navigateUp()
            }
        }
    }
}


@Composable
fun AuroraBottomNavigation(navController: NavHostController, bottomNavList: List<BottomEntry>) {

    var hideBottomNav by rememberBooleanSaveableDefaultFalse()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    navBackStackEntry?.debug { "CURRENT ROUTE $currentRoute" }

    val size = if (hideBottomNav) {
        Modifier.size(animateDpAsState(targetValue = 0.dp, animationSpec = tween()).value)
    } else {
        Modifier
    }

    BottomNavigation(
        modifier = size
            .clip(BottomSheetShapes.large)
            .navigationBarsPadding()
    ) {
        hideBottomNav = currentRoute in BottomNav.hideBottomNavOnDestinations
        bottomNavList.forEach { bottomEntry ->
            BottomNavigationItem(
                selected = currentRoute == bottomEntry.screen.route,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(bottomEntry.screen.route) {
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
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

