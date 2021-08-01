package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bookdetailsui.ShowDetailedBook
import com.funkymuse.aurora.bottomnavigation.AuroraBottomNavigation
import com.funkymuse.aurora.bottomnavigation.BottomNav
import com.funkymuse.aurora.bottomnavigation.destinations.FavoritesBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.LatestBooksBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SearchBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SettingsBottomNavRoute
import com.funkymuse.aurora.crashesdestination.CrashesDestination
import com.funkymuse.aurora.crashesui.Crashes
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
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var navigator: Navigator

    private val isDarkThemeEnabled get() = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AuroraTheme(darkThemeFlow = hiltViewModel<SettingsViewModel>().darkTheme, isDarkThemeEnabled) {
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
    LaunchedEffect(navController) {
        navigator.destinations.collect {
            when (val event = it) {
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
                    addBookDetails()
                    addCrashes()
                }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun NavGraphBuilder.addCrashes() {
    composable(CrashesDestination.route()) {
        Crashes()
    }
}

private fun NavGraphBuilder.addSearch() {
    composable(SearchBottomNavRoute.route) {
        Search()
    }
}

private fun NavGraphBuilder.addFavorites() {
    composable(FavoritesBottomNavRoute.route) {
        Favorites()
    }
}

private fun NavGraphBuilder.addLatestBooks() {
    composable(LatestBooksBottomNavRoute.route) {
        LatestBooks()
    }
}

private fun NavGraphBuilder.addSettings() {
    composable(SettingsBottomNavRoute.route) {
        Settings()
    }
}


private fun NavGraphBuilder.addSearchResult() {
    with(SearchResultDestination) {
        composable(route(), arguments) {
            SearchResult()
        }
    }
}


private fun NavGraphBuilder.addBookDetails() {
    with(BookDetailsDestination) {
        composable(route(), arguments) {
            ShowDetailedBook()
        }
    }
}

