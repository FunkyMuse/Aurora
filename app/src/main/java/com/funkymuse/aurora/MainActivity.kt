package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination.addBookMirrors
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination.getBookMirrors
import com.funkymuse.aurora.bookdetailsui.ShowDetailedBook
import com.funkymuse.aurora.bottomnavigation.AuroraBottomNavigation
import com.funkymuse.aurora.bottomnavigation.BottomNav
import com.funkymuse.aurora.bottomnavigation.destinations.FavoritesBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.LatestBooksBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SearchBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SettingsBottomNavRoute
import com.funkymuse.aurora.favoritebookui.Favorites
import com.funkymuse.aurora.latestbooksui.LatestBooks
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.navigator.NavigatorEvent
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination
import com.funkymuse.aurora.searchresultui.SearchResult
import com.funkymuse.aurora.searchui.Search
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.aurora.settingsui.Settings
import com.funkymuse.style.theme.AuroraTheme
import com.google.accompanist.coil.LocalImageLoader
import com.google.accompanist.insets.ProvideWindowInsets
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
    LaunchedEffect(rememberCoroutineScope()) {
        navigator.destinations.collectLatest {
            when (val event = it) {
                null -> return@collectLatest
                is NavigatorEvent.NavigateUp -> navController.navigateUp()
                is NavigatorEvent.Directions -> navController.navigate(event.destination, event.builder)
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
                    addSearch()
                    addFavorites()
                    addLatestBooks()
                    addSettings()
                    addSearchResult()
                    addBookDetails(navController)
                }
        )
    }
}

private fun NavGraphBuilder.addSearch() {
    composable(SearchBottomNavRoute.route) {
        Search()
    }
}

private fun NavGraphBuilder.addFavorites() {
    composable(FavoritesBottomNavRoute.route) {
        Favorites { mirrors ->
            it.addBookMirrors(mirrors)
        }
    }
}

private fun NavGraphBuilder.addLatestBooks() {
    composable(LatestBooksBottomNavRoute.route) {
        LatestBooks { mirrors ->
            it.addBookMirrors(mirrors)
        }
    }
}

private fun NavGraphBuilder.addSettings() {
    composable(SettingsBottomNavRoute.route) {
        Settings()
    }
}


private fun NavGraphBuilder.addSearchResult() {
    val destination = SearchResultDestination.destination
    composable(destination.route(), destination.arguments) {
        SearchResult { mirrors ->
            it.addBookMirrors(mirrors)
        }
    }
}


private fun NavGraphBuilder.addBookDetails(
        navController: NavHostController
) {
    val destination = BookDetailsDestination.destination
    composable(destination.route(), destination.arguments) {
        it.arguments?.apply {
            //workaround since we can't pass parcelable as nav arguments :(
            ShowDetailedBook(navController.previousBackStackEntry?.getBookMirrors())
        }
    }
}

