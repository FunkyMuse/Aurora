package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.crazylegend.kotlinextensions.log.debug
import com.funkymuse.aurora.bookDetails.BookDetailsDestination
import com.funkymuse.aurora.bookDetails.BookDetailsDestination.BOOK_MIRRORS_PARAM
import com.funkymuse.aurora.bookDetails.ShowDetailedBook
import com.funkymuse.aurora.bottomnavigation.BottomEntry
import com.funkymuse.aurora.bottomnavigation.BottomNav
import com.funkymuse.aurora.bottomnavigation.destinations.FavoritesBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.LatestBooksBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SearchBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SettingsBottomNavRoute
import com.funkymuse.aurora.favorites.Favorites
import com.funkymuse.aurora.latestBooks.LatestBooks
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.navigator.NavigatorEvent
import com.funkymuse.aurora.search.Search
import com.funkymuse.aurora.searchResult.*
import com.funkymuse.aurora.settings.Settings
import com.funkymuse.aurora.settings.SettingsViewModel
import com.funkymuse.composed.core.rememberBooleanSaveableDefaultFalse
import com.funkymuse.style.shape.BottomSheetShapes
import com.funkymuse.style.theme.AuroraTheme
import com.google.accompanist.coil.LocalImageLoader
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AuroraTheme(darkThemeFlow = hiltViewModel<SettingsViewModel>().darkTheme) {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                        Surface(color = MaterialTheme.colors.background) {
                            AuroraScaffold(navigator)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuroraScaffold(navigator: Navigator) {

    val navController = rememberNavController()

    navigator.destinations.collectAsState()
    LaunchedEffect(key1 = rememberCoroutineScope()) {
        navigator.destinations.collectLatest {
            val event = it ?: return@collectLatest

            debug { "NAVIGATOR EVENT $this" }
            when (event) {
                is NavigatorEvent.NavigateUp -> navController.navigateUp()
                is NavigatorEvent.Directions -> navController.navigate(event.destination.route()) { launchSingleTop = true }
            }
        }
    }

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
                        Search()
                    }
                    composable(FavoritesBottomNavRoute.route) {
                        Favorites { mirrors ->
                            it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
                        }
                    }
                    composable(LatestBooksBottomNavRoute.route) {
                        LatestBooks { mirrors ->
                            it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
                        }
                    }
                    composable(SettingsBottomNavRoute.route) {
                        Settings()
                    }
                    addSearchResult()
                    addBookDetails(navController)
                }
        )
    }
}


private fun NavGraphBuilder.addSearchResult() {
    val destination = SearchResultDestination.destination
    composable(destination.route(), destination.arguments) {
        SearchResult { mirrors ->
            it.arguments?.putParcelable(BOOK_MIRRORS_PARAM, mirrors)
        }
    }
}

private fun NavGraphBuilder.addBookDetails(
        navController: NavHostController,
) {
    val destination = BookDetailsDestination.destination
    composable(destination.route(), destination.arguments) {
        it.arguments?.apply {
            //workaround since we can't pass parcelable as nav arguments :(
            ShowDetailedBook(navController.previousBackStackEntry?.arguments?.getParcelable(BOOK_MIRRORS_PARAM))
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

