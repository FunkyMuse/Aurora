package com.funkymuse.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.plusAssign
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.funkymuse.aurora.bottomnavigation.AuroraBottomNavigation
import com.funkymuse.aurora.bottomnavigation.SearchRoute
import com.funkymuse.aurora.composeextensions.AssistedHiltInjectables
import com.funkymuse.aurora.composeextensions.assistedInjectable
import com.funkymuse.aurora.donationsexplanationdestination.DONATE_PREFS_KEY
import com.funkymuse.aurora.donationsexplanationdestination.DONATE_RESET_KEY
import com.funkymuse.aurora.donationsexplanationdestination.DonationsExplanationDestination
import com.funkymuse.aurora.donationsui.USER_DONATED_KEY
import com.funkymuse.aurora.navigation.addBottomNavigationDestinations
import com.funkymuse.aurora.navigation.addBottomSheetDestinations
import com.funkymuse.aurora.navigation.addComposableDestinations
import com.funkymuse.aurora.navigation.addDialogDestinations
import com.funkymuse.aurora.navigator.AuroraNavigator
import com.funkymuse.aurora.navigator.AuroraNavigatorViewModel
import com.funkymuse.aurora.navigator.NavigatorEvent
import com.funkymuse.aurora.onetimepreferences.OneTimePreferencesViewModel
import com.funkymuse.aurora.runcodeeveryxlaunch.RunCodePreferencesViewModel
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.composed.core.collectAndRemember
import com.funkymuse.style.shape.BottomSheetShapes
import com.funkymuse.style.theme.AuroraTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), AssistedHiltInjectables {

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var auroraNavigator: AuroraNavigator

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
                            AuroraScaffold(auroraNavigator)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalAnimationApi::class,
    com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi::class
)
@Composable
fun AuroraScaffold(auroraNavigator: AuroraNavigator) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController()
    navController.navigatorProvider += bottomSheetNavigator

    LaunchedEffect(navController) {
        auroraNavigator.destinations.collect {
            when (val event = it) {
                is NavigatorEvent.NavigateUp -> navController.navigateUp()
                is NavigatorEvent.Directions -> navController.navigate(
                    event.destination,
                    event.builder
                )
            }
        }
    }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = BottomSheetShapes.medium
    ) {
        Scaffold(
            bottomBar = {
                AuroraBottomNavigation(navController)
            }
        ) { paddingValues ->
            AnimatedNavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = SearchRoute.route,
                enterTransition = { fadeIn(animationSpec = tween(0)) },
                exitTransition = { fadeOut(animationSpec = tween(0)) },
            ) {
                addDialogDestinations(navController)
                addComposableDestinations(navController)
                addBottomNavigationDestinations(navController)
                addBottomSheetDestinations(navController)
            }
        }
    }


    UserDonationsInfo()
    ResetUserDonationsInfo()
}

@Composable
fun UserDonationsInfo() {
    val navigator = hiltViewModel<AuroraNavigatorViewModel>()
    val oneTimePreferencesViewModel = assistedInjectable(produce = {
        oneTimePreferencesViewModelFactory.create(USER_DONATED_KEY)
    })
    val oneTimePreferenceUserDonated by oneTimePreferencesViewModel.isEventFired.collectAndRemember(
        initial = false
    )
    val runCodePreferenceRemindUserDonated = assistedInjectable(produce = {
        runCodePreferencesViewModelFactory.create(DONATE_PREFS_KEY, 4)
    })

    val runCode by runCodePreferenceRemindUserDonated.runCode.collectAndRemember(false)
    if (runCode && !oneTimePreferenceUserDonated) {
        navigator.navigate(DonationsExplanationDestination.route())
    }
}

@Composable
fun ResetUserDonationsInfo() {
    val runCodePreferenceRemindUserDonated = assistedInjectable(produce = {
        runCodePreferencesViewModelFactory.create(DONATE_RESET_KEY, 30)
    })

    val oneTimePreferencesViewModel = assistedInjectable(produce = {
        oneTimePreferencesViewModelFactory.create(USER_DONATED_KEY)
    })

    val runCode by runCodePreferenceRemindUserDonated.runCode.collectAndRemember(false)
    if (runCode) {
        oneTimePreferencesViewModel.setEventIsNotFired()
    }
}


