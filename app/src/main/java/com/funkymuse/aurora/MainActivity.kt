package com.funkymuse.aurora

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
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
        hideNavigationBars()
    }

    @Suppress("DEPRECATION")
    private fun hideNavigationBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController ?: return
            controller.hide(WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

            val decorView = window.decorView

            decorView.systemUiVisibility = flags
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    decorView.systemUiVisibility = flags
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

