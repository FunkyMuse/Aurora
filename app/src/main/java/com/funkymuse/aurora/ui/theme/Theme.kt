package com.funkymuse.aurora.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import com.funkymuse.aurora.settings.SettingsViewModel
import com.funkymuse.composed.core.stateWhenStarted
import kotlinx.coroutines.flow.firstOrNull

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
    val scope = rememberCoroutineScope()
    var isSystemInDark = isSystemInDarkTheme()

    LaunchedEffect(scope) {
        settingsViewModel.darkTheme.firstOrNull()?.let { isSystemInDark = it }
    }

    val darkTheme by stateWhenStarted(
        flow = settingsViewModel.darkTheme,
        initial = isSystemInDark
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