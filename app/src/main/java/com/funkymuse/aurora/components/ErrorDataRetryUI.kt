package com.funkymuse.aurora.components

import androidx.annotation.RawRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.funkymuse.aurora.R
import com.funkymuse.aurora.bookDetails.TopAppBarBackOnly
import com.funkymuse.aurora.ui.theme.Primary
import com.funkymuse.aurora.ui.theme.Secondary
import kotlin.math.*

/**
 * Created by Hristijan, date 3/3/21
 */
@Preview
@Composable
fun ErrorWidget(modifier: Modifier = Modifier) {

    val infiniteTransition = rememberInfiniteTransition()


    val color by infiniteTransition.animateColor(
        initialValue = Primary,
        targetValue = Secondary,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1500
                13f at 400
                15f at 800 / 2
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(36.dp)
            .animateContentSize()
    ) {
        val (width, height) = size

        val rightLineOffsetEnd = Offset(width, height / 2)
        val startingPoint = Offset(width / 2, 0f)
        drawLine(
            color = color,
            start = startingPoint,
            end = rightLineOffsetEnd,
            strokeWidth = scale,
            cap = StrokeCap.Round
        )

        val leftLineHeightEnd = Offset(0f, height / 2)
        drawLine(
            color = color,
            start = startingPoint,
            end = leftLineHeightEnd,
            strokeWidth = scale,
            cap = StrokeCap.Round
        )

        val startMiddleOffset = Offset(startingPoint.x / 2, height / 4)
        drawLine(
            color = color,
            start = startMiddleOffset,
            end = Offset((startingPoint.x / 2) * 3, startMiddleOffset.y),
            strokeWidth = scale,
            cap = StrokeCap.Round
        )
    }
}


@Composable
fun ScaffoldWithBack(
    showRetry: Boolean = false,
    onRetryClicked: () -> Unit = {},
    onBackClicked: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBarBackOnly(onBackClicked)
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /*LottieAnim(
                modifier = Modifier.align(Alignment.CenterHorizontally), anim = anim
            )*/

            if (showRetry) {
                RetryOption(onRetryClicked)
            }
        }
    }
}

@Composable
fun ScaffoldWithBackAndContent(
    onBackClicked: () -> Unit,
    content: @Composable() (PaddingValues) -> Unit
) {
    Scaffold(topBar = {
        TopAppBarBackOnly(onBackClicked)
    }) {
        content(it)
    }
}

@Composable
fun ErrorWithRetry(
    @RawRes anim: Int = R.raw.server_error,
    showRetry: Boolean = false,
    onRetryClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*LottieAnim(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(36.dp), anim = anim
        )*/
        if (showRetry) {
            RetryOption(onRetryClicked)
        }
    }
}

@Composable
@Preview
fun RetryOption(onRetryClicked: () -> Unit = {}) {
    Column(modifier = Modifier.clickable {
        onRetryClicked()
    }) {
        Icon(
            imageVector = Icons.Filled.Replay,
            contentDescription = stringResource(id = R.string.retry),
            modifier = Modifier
                .size(50.dp)
        )
        Text(text = stringResource(id = R.string.retry), modifier = Modifier.padding(8.dp))
    }
}

