package com.funkymuse.style.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.funkymuse.style.color.*
import com.funkymuse.style.shape.Shapes
import com.funkymuse.style.typography.Typography
import kotlinx.coroutines.flow.Flow

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

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AuroraTheme(darkThemeFlow: Flow<Boolean>, defaultValue: Boolean, content: @Composable () -> Unit) {
    val isSystemInDark by darkThemeFlow.collectAsStateWithLifecycle(defaultValue)

    val colors = if (isSystemInDark) {
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

