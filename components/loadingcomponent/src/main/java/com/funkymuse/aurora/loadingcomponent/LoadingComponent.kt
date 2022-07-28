package com.funkymuse.aurora.loadingcomponent

import androidx.annotation.StringRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource

/**
 * Created by funkymuse, date 4/14/21
 */


@Composable
fun LoadingBubbles(
    @StringRes text: Int = R.string.loading,
    colorDurationTransition: Int = 3000,
    scaleDuration: Int = 2000
) {
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colors.secondary,
        targetValue = MaterialTheme.colors.primary,
        animationSpec = infiniteRepeatable(
            animation = tween(colorDurationTransition, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(scaleDuration),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val (width, height) = size

        val radius = size.minDimension * scale / 6f

        drawCircle(color, radius = radius)
        //bottom right
        translate(left = width / 2, height / 2f) {
            drawCircle(color, radius = radius)
        }

        //bottom left
        translate(left = -width / 2, height / 2f) {
            drawCircle(color, radius = radius)
        }

        //top left
        translate(left = -width / 2, -height / 2f) {
            drawCircle(color, radius = radius)
        }

        //top right
        translate(left = width / 2, -height / 2f) {
            drawCircle(color, radius = radius)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = text), Modifier.graphicsLayer(scale, scale))
    }
}

