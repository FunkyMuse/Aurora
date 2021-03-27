package com.funkymuse.aurora.ui.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import com.crazylegend.kotlinextensions.context.isDarkTheme
import com.crazylegend.kotlinextensions.misc.enableNightMode
import com.funkymuse.aurora.extensions.stateWhenStarted
import com.funkymuse.aurora.settings.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val DarkColorPalette = darkColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant
)

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    surface = Surface,
    secondaryVariant = SecondaryVariant


    /* Other default colors to override
background = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun AuroraTheme(content: @Composable () -> Unit) {
    val settingsViewModel = hiltNavGraphViewModel<SettingsViewModel>()
    val darkTheme by stateWhenStarted(
        flow = settingsViewModel.darkTheme,
        initial = isSystemInDarkTheme()
    )

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}