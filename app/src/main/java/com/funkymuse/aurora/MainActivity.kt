package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.bookDetails.*
import com.funkymuse.aurora.bottomnavigation.BottomEntry
import com.funkymuse.aurora.bottomnavigation.BottomNav
import com.funkymuse.aurora.bottomnavigation.BottomNavScreen
import com.funkymuse.aurora.favorites.Favorites
import com.funkymuse.aurora.latestBooks.LatestBooks
import com.funkymuse.aurora.search.Search
import com.funkymuse.aurora.searchResult.*
import com.funkymuse.aurora.settings.Settings
import com.funkymuse.aurora.ui.theme.AuroraTheme
import com.funkymuse.aurora.ui.theme.BottomSheetShapes
import com.funkymuse.composed.core.rememberBooleanSaveableDefaultFalse
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimatedInsets
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AuroraTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Surface(color = MaterialTheme.colors.background) {
                        AuroraScaffold()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
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
            startDestination = BottomNavScreen.Search.route,
            builder = {
                composable(BottomNavScreen.Search.route) {
                    Search(it) { inputText, searchInFieldsCheckedPosition, searchWithMaskWord ->
                        openSearchResult(
                            navController,
                            inputText.trim(),
                            searchInFieldsCheckedPosition,
                            searchWithMaskWord
                        )
                    }
                }
                composable(BottomNavScreen.Favorites.route) {
                    Favorites(it) { id, mirrors ->
                        it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
                        openDetailedBook(navController, id)
                    }
                }
                composable(BottomNavScreen.LatestBooks.route) {
                    LatestBooks(it) { id, mirrors ->
                        it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
                        openDetailedBook(navController, id)
                    }
                }
                composable(BottomNavScreen.Settings.route) {
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
    navController.navigate("$SEARCH_RESULT_ROUTE/$inputText/$searchInFieldsCheckedPosition/$searchWithMaskWord") {
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
        arguments = listOf(
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
    ) {
        SearchResult({ navController.navigateUp() }) { id: Int, mirrors ->
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
                navController,
            )
        }
    }
}


@Composable
fun AuroraBottomNavigation(navController: NavHostController, bottomNavList: List<BottomEntry>) {

    var hideBottomNav by rememberBooleanSaveableDefaultFalse()
    val size = if (hideBottomNav) {
        Modifier.size(animateDpAsState(targetValue = 0.dp, animationSpec = tween()).value)
    } else {
        Modifier
    }

    BottomNavigation(modifier = size
        .clip(BottomSheetShapes.large)
        .navigationBarsPadding()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        debug { "CURRENT ROUTE $currentRoute" }
        hideBottomNav = currentRoute in BottomNav.hideBottomNavOnDestinations
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

