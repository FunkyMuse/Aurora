package com.funkymuse.aurora.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funkymuse.aurora.R
import com.funkymuse.aurora.bookDetails.TopAppBarBackOnly
import com.funkymuse.aurora.ui.theme.Primary
import com.funkymuse.aurora.ui.theme.Secondary

/**
 * Created by Hristijan, date 3/3/21
 */
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
    ) {
        val (width, height) = size

        val rightLineOffsetEnd = Offset(width, height)
        val startingPoint = Offset(width / 2, 0f)
        drawLine(
            color = color,
            start = startingPoint,
            end = rightLineOffsetEnd,
            strokeWidth = scale,
            cap = StrokeCap.Round
        )

        val leftLineHeightEnd = Offset(0f, height)
        drawLine(
            color = color,
            start = startingPoint,
            end = leftLineHeightEnd,
            strokeWidth = scale,
            cap = StrokeCap.Round
        )

        val startMiddleOffset = Offset(startingPoint.x / 2, height / 2)
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
            ErrorWidget()

            if (showRetry) {
                RetryOption(onRetryClicked)
            }
        }
    }
}

@Composable
fun ScaffoldWithBackAndContent(
    onBackClicked: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarBackOnly(onBackClicked)
        }) {
        content(it)
    }
}


@Composable
fun ErrorWithRetry(
    @StringRes text: Int,
    onRetryClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ErrorWidget(Modifier.size(width = 250.dp, 250.dp))
        Text(
            text = stringResource(id = text),
            textAlign = TextAlign.Center, fontSize = 24.sp,
            modifier = Modifier.padding(24.dp)
        )
        RetryOption(onRetryClicked)
    }
}

@Composable
fun ErrorMessage(
    @StringRes text: Int,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ErrorWidget(Modifier.size(width = 250.dp, 250.dp))
        Text(
            text = stringResource(id = text),
            textAlign = TextAlign.Center, fontSize = 24.sp,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
@Preview
fun ErrorWithRetry() {
    ErrorWithRetry(text = R.string.no_book_loaded_no_connect)
}

@Composable
@Preview
fun RetryOption(onRetryClicked: () -> Unit = {}) {
    Column(modifier = Modifier
        .clickable {
            onRetryClicked()
        }
        .padding(top = 16.dp)) {
        Icon(
            imageVector = Icons.Filled.Replay,
            contentDescription = stringResource(id = R.string.retry),
            modifier = Modifier
                .size(50.dp)
        )
        Text(text = stringResource(id = R.string.retry), modifier = Modifier.padding(8.dp))
    }
}

