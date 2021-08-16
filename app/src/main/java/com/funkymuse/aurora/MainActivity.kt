package com.funkymuse.aurora

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.funkymuse.aurora.internetdetector.connectiontype.ConnectionType
import com.funkymuse.aurora.internetdetector.connectiontype.ConnectionTypeViewModel
import com.funkymuse.aurora.navigation.addBottomNavigationDestinations
import com.funkymuse.aurora.navigation.addDestinations
import com.funkymuse.aurora.navigator.Navigator
import com.funkymuse.aurora.navigator.NavigatorEvent
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.style.theme.AuroraTheme
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
        topBar = {
            TopAppBarIndicator()
        },
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

@Composable
private fun TopAppBarIndicator() {
    val paddings = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.statusBars,
    applyBottom = false, additionalTop = 4.dp)
    val connectionTypeViewModel = hiltViewModel<ConnectionTypeViewModel>()
    val state = connectionTypeViewModel.collectAsState(initial = ConnectionType.Idle)
    var hideTopIndicator by remember { mutableStateOf(false) }


    val size = if (hideTopIndicator) {
        Modifier.size(
            animateDpAsState(
                targetValue = 0.dp,
                animationSpec = tween(easing = LinearOutSlowInEasing)
            ).value
        )
    } else {
        Modifier
    }

    val delayAmount = when (state.value) {
        ConnectionType.MobileData, ConnectionType.Ethernet,
        ConnectionType.VPN, ConnectionType.WiFi, ConnectionType.Idle, ConnectionType.NoInfo
        -> 5000L
        else -> null
    }

    LaunchedEffect(state.value) {
        hideTopIndicator = false
        delayAmount?.let {
            delay(it)
            hideTopIndicator = true
        }
    }
    val text = when (state.value) {
        ConnectionType.MobileData -> R.string.connection_mobile_data
        ConnectionType.NoConnection -> R.string.no_connection_message
        ConnectionType.VPN -> R.string.connection_vpn
        ConnectionType.WiFi -> R.string.connection_wifi
        ConnectionType.Ethernet -> R.string.connection_ethernet
        else -> R.string.connection_searching
    }
    val color = when (state.value) {
        ConnectionType.MobileData, ConnectionType.WiFi -> Color.Green
        ConnectionType.NoConnection -> Color.Red
        ConnectionType.VPN -> Color.DarkGray
        else -> Color.Yellow
    }
    Text(
        text = stringResource(text), modifier = size
            .fillMaxWidth()
            .background(color)
            .padding(paddings)
            .padding(bottom = 4.dp),
        textAlign = TextAlign.Center
    )
}




