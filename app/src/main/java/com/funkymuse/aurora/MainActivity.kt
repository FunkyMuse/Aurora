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
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bottomnavigation.AuroraBottomNavigation
import com.funkymuse.aurora.bottomnavigation.SearchRoute
import com.funkymuse.aurora.composeextensions.AssistedHiltInjectibles
import com.funkymuse.aurora.composeextensions.assistedInjectable
import com.funkymuse.aurora.crashesdestination.CrashesDestination
import com.funkymuse.aurora.donationsdestination.DonateDestination
import com.funkymuse.aurora.donationsexplanationdestination.DONATE_PREFS_KEY
import com.funkymuse.aurora.donationsexplanationdestination.DONATE_RESET_KEY
import com.funkymuse.aurora.donationsexplanationdestination.DonationsExplanationDestination
import com.funkymuse.aurora.donationsui.USER_DONATED_KEY
import com.funkymuse.aurora.navigation.addBottomNavigationDestinations
import com.funkymuse.aurora.navigation.addComposableDestinations
import com.funkymuse.aurora.navigation.addDialogDestinations
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.navigator.NavigatorEvent
import com.funkymuse.aurora.navigator.NavigatorViewModel
import com.funkymuse.aurora.onetimepreferences.OneTimePreferencesViewModel
import com.funkymuse.aurora.runcodeeveryxlaunch.RunCodePreferencesViewModel
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.style.theme.AuroraTheme
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), AssistedHiltInjectibles {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var navigator: Navigator

    private val isDarkThemeEnabled get() = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    @Inject
    override lateinit var runCodePreferencesViewModelFactory: RunCodePreferencesViewModel.RunCodePreferencesViewModelFactory

    @Inject
    override lateinit var oneTimePreferencesViewModelFactory: OneTimePreferencesViewModel.OneTimePreferencesViewModelFactory

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
                addComposableDestinations()
                addDialogDestinations()
                addBottomNavigationDestinations()
            }
        )
    }

    UserDonationsInfo()
    ResetUserDonationsInfo()
}

@Composable
fun UserDonationsInfo() {
    val navigator = hiltViewModel<NavigatorViewModel>()
    val oneTimePreferencesViewModel = assistedInjectable(produce = {
        oneTimePreferencesViewModelFactory.create(USER_DONATED_KEY)
    })
    val oneTimePreferenceUserDonated = oneTimePreferencesViewModel.isEventFired.collectAsState().value

    val runCodePreferenceRemindUserDonated = assistedInjectable(produce = {
        runCodePreferencesViewModelFactory.create(DONATE_PREFS_KEY, 4)
    })

    val runCode = runCodePreferenceRemindUserDonated.runCode.collectAsState()
    if (runCode.value && !oneTimePreferenceUserDonated) {
        navigator.navigate(DonationsExplanationDestination.route())
    }
}

@Composable
fun ResetUserDonationsInfo(){
    val runCodePreferenceRemindUserDonated = assistedInjectable(produce = {
        runCodePreferencesViewModelFactory.create(DONATE_RESET_KEY, 30)
    })

    val oneTimePreferencesViewModel = assistedInjectable(produce = {
        oneTimePreferencesViewModelFactory.create(USER_DONATED_KEY)
    })

    val runCode = runCodePreferenceRemindUserDonated.runCode.collectAsState()
    if (runCode.value){
        oneTimePreferencesViewModel.setEventIsNotFired()
    }
}


