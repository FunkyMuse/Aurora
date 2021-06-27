package com.funkymuse.style.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.funkymuse.composed.core.stateWhenStarted
import com.funkymuse.style.color.*
import com.funkymuse.style.shape.Shapes
import com.funkymuse.style.typography.Typography
import kotlinx.coroutines.flow.Flow
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
fun AuroraTheme(darkThemeFlow: Flow<Boolean>, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    var isSystemInDark = isSystemInDarkTheme()

    LaunchedEffect(scope) {
        darkThemeFlow.firstOrNull()?.let { isSystemInDark = it }
    }

    val darkTheme by stateWhenStarted(
        flow = darkThemeFlow,
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

