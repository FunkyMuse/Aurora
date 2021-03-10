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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.bookDetails.*
import com.funkymuse.aurora.extensions.rememberBooleanSaveableDefaultFalse
import com.funkymuse.aurora.favorites.Favorites
import com.funkymuse.aurora.latestBooks.LatestBooks
import com.funkymuse.aurora.search.Search
import com.funkymuse.aurora.searchResult.*
import com.funkymuse.aurora.settings.Settings
import com.funkymuse.aurora.ui.theme.AuroraTheme
import com.funkymuse.aurora.ui.theme.BottomSheetShapes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bookDetailsViewModelFactory: BookDetailsViewModel.BookDetailsVMF

    @Inject
    lateinit var searchResultVMF: SearchResultVM.SearchResultVMF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroraTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AuroraScaffold(bookDetailsViewModelFactory, searchResultVMF)
                }
            }
        }
    }
}

data class BottomEntry(val screen: BottomNavScreen, val icon: ImageVector)

@Composable
fun AuroraScaffold(
    bookDetailsViewModelFactory: BookDetailsViewModel.BookDetailsVMF,
    searchResultVMF: SearchResultVM.SearchResultVMF
) {

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
            )
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
                    Search(it) { inputText, searchInCheckedPosition, searchInFieldsCheckedPosition, searchWithMaskWord ->
                        openSearchResult(
                            navController,
                            inputText,
                            searchInCheckedPosition,
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
                    Settings(navController)
                }
                addSearchResult(navController, searchResultVMF)
                addBookDetails(navController, bookDetailsViewModelFactory)
            }
        )
    }
}

fun openSearchResult(
    navController: NavHostController,
    inputText: String,
    searchInCheckedPosition: Int,
    searchInFieldsCheckedPosition: Int,
    searchWithMaskWord: Boolean
) {
    navController.navigate("$SEARCH_RESULT_ROUTE/$inputText/$searchInCheckedPosition/$searchInFieldsCheckedPosition/$searchWithMaskWord") {
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
    searchResultVMF: SearchResultVM.SearchResultVMF
) {
    composable(
        SEARCH_ROUTE_BOTTOM_NAV,
        arguments = listOf(
            navArgument(SEARCH_PARAM) { type = NavType.StringType },
            navArgument(SEARCH_IN_PARAM) {
                type = NavType.IntType
                defaultValue = 0
            },
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
        val query = it.arguments?.getString(SEARCH_PARAM).toString()
        val searchInCheckedPosition = it.arguments?.getInt(SEARCH_IN_PARAM) ?: 0
        val searchInFieldsCheckedPosition = it.arguments?.getInt(SEARCH_IN_FIELDS_PARAM) ?: 0
        val searchWithMaskWord =
            it.arguments?.getBoolean(SEARCH_WITH_MASK_WORD_PARAM, false) ?: false
        SearchResult(
            searchResultVMF,
            query,
            searchInCheckedPosition,
            searchInFieldsCheckedPosition,
            searchWithMaskWord
        ) { id: Int, mirrors ->
            openDetailedBook(navController, id)
        }
    }
}

private fun NavGraphBuilder.addBookDetails(
    navController: NavHostController,
    bookDetailsViewModelFactory: BookDetailsViewModel.BookDetailsVMF
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

    BottomNavigation(modifier = size.clip(BottomSheetShapes.large)) {
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

