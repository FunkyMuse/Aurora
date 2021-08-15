package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bottomnavigation.AuroraBottomNavigation
import com.funkymuse.aurora.bottomnavigation.SearchRoute
import com.funkymuse.aurora.crashesdestination.CrashesDestination
import com.funkymuse.aurora.donationsdestination.DonateDestination
import com.funkymuse.aurora.navigation.addBottomNavigationDestinations
import com.funkymuse.aurora.navigation.addDestinations
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.navigator.NavigatorEvent
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination
import com.funkymuse.aurora.settingsdata.SettingsViewModel
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
            AuroraTheme(
                darkThemeFlow = hiltViewModel<SettingsViewModel>().darkTheme,
                isDarkThemeEnabled
            ) {
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

private val hideBottomNavFromDestinationRoutes = listOf(
    CrashesDestination.route(),
    BookDetailsDestination.route(),
    SearchResultDestination.route(),
    DonateDestination.route()
)

@Composable
fun AuroraScaffold(navigator: Navigator) {
    val navController = rememberNavController()
    LaunchedEffect(navController) {
        navigator.destinations.collect {
            when (val event = it) {
                is NavigatorEvent.NavigateUp -> navController.navigateUp()
                is NavigatorEvent.Directions -> navController.navigate(
                    event.destination,
                    event.builder
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            AuroraBottomNavigation(navController, hideBottomNavFromDestinationRoutes)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = SearchRoute.route,
            builder = {
                addDestinations()
                addBottomNavigationDestinations()
            }
        )
    }
}




