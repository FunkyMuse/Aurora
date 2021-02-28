package com.funkymuse.aurora.extensions

import android.os.Bundle
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by FunkyMuse, date 2/27/21
 */

@Composable
fun CardListShimmer(
    imageHeight: Dp = 160.dp,
    padding: Dp = 16.dp,
    shimmerDelayDuration: Int = 300,
    shimmerDuration: Int = 1600,
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = .9f),
        Color.LightGray.copy(alpha = .3f),
        Color.LightGray.copy(alpha = .9f),
    ),
    alphaDuration: Int = 400,
    itemsCount: Int = Integer.MAX_VALUE,
    cardShape: Shape = MaterialTheme.shapes.large
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        val cardWidthPx = with(LocalDensity.current) { (maxWidth - (padding * 2)).toPx() }
        val cardHeightPx = with(LocalDensity.current) { (imageHeight - padding).toPx() }
        val gradientWidth: Float = (0.2f * cardHeightPx)
        val tweenAnim = tweenParameters(shimmerDuration, shimmerDelayDuration)
        val infiniteTransition = rememberInfiniteTransition()

        val xCardShimmer = cardShimmerAxis(
            cardWidthPx = cardWidthPx,
            infiniteTransition = infiniteTransition,
            gradientWidth = gradientWidth,
            tweenAnim = tweenAnim
        )

        val yCardShimmer = cardShimmerAxis(
            cardWidthPx = cardHeightPx,
            infiniteTransition = infiniteTransition,
            gradientWidth = gradientWidth,
            tweenAnim = tweenAnim
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = alphaDuration
                    0.7f at alphaDuration / 2
                },
                repeatMode = RepeatMode.Reverse
            )
        )

        LazyColumn {
            items(itemsCount) {
                ShimmerCardItem(
                    colors = colors,
                    xShimmer = xCardShimmer.value,
                    yShimmer = yCardShimmer.value,
                    cardHeight = imageHeight,
                    gradientWidth = gradientWidth,
                    padding = padding,
                    alpha = alpha,
                    cardShape = cardShape
                )
            }
        }
    }
}


@Composable
fun CardShimmer(
    imageHeight: Dp = 160.dp,
    imageWidth: Dp? = null,
    padding: Dp = 16.dp,
    shimmerDelayDuration: Int = 300,
    shimmerDuration: Int = 1600,
    colors: List<Color> = listOf(
        Color.LightGray.copy(alpha = .9f),
        Color.LightGray.copy(alpha = .3f),
        Color.LightGray.copy(alpha = .9f),
    ),
    alphaDuration: Int = 400,
    cardShape: Shape = MaterialTheme.shapes.large
) {
    val modifier = if (imageWidth != null) {
        Modifier.size(imageWidth, imageHeight)
    } else {
        Modifier.fillMaxSize()
    }
    BoxWithConstraints(
        modifier = modifier
    ) {

        val cardWidthPx = with(LocalDensity.current) { (maxWidth - (padding * 2)).toPx() }
        val cardHeightPx = with(LocalDensity.current) { (imageHeight - padding).toPx() }
        val gradientWidth: Float = (0.2f * cardHeightPx)
        val tweenAnim = tweenParameters(shimmerDuration, shimmerDelayDuration)
        val infiniteTransition = rememberInfiniteTransition()

        val xCardShimmer = cardShimmerAxis(
            cardWidthPx = cardWidthPx,
            infiniteTransition = infiniteTransition,
            gradientWidth = gradientWidth,
            tweenAnim = tweenAnim
        )

        val yCardShimmer = cardShimmerAxis(
            cardWidthPx = cardHeightPx,
            infiniteTransition = infiniteTransition,
            gradientWidth = gradientWidth,
            tweenAnim = tweenAnim
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = alphaDuration
                    0.7f at alphaDuration / 2
                },
                repeatMode = RepeatMode.Reverse
            )
        )

        ShimmerCardItem(
            colors = colors,
            xShimmer = xCardShimmer.value,
            yShimmer = yCardShimmer.value,
            cardHeight = imageHeight,
            gradientWidth = gradientWidth,
            padding = padding,
            alpha = alpha,
            cardShape = cardShape
        )
    }
}


@Composable
private fun cardShimmerAxis(
    cardWidthPx: Float,
    infiniteTransition: InfiniteTransition,
    gradientWidth: Float,
    tweenAnim: DurationBasedAnimationSpec<Float>
): State<Float> {
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (cardWidthPx + gradientWidth),
        animationSpec = infiniteRepeatable(
            animation = tweenAnim,
            repeatMode = RepeatMode.Restart
        )
    )
}

private fun tweenParameters(
    shimmerDuration: Int,
    shimmerDelayDuration: Int
): DurationBasedAnimationSpec<Float> {
    return tween(
        durationMillis = shimmerDuration,
        easing = LinearEasing,
        delayMillis = shimmerDelayDuration
    )
}

@Composable
private fun ShimmerCardItem(
    colors: List<Color>,
    xShimmer: Float,
    yShimmer: Float,
    cardHeight: Dp,
    gradientWidth: Float,
    padding: Dp,
    alpha: Float = 1f,
    cardShape: Shape
) {
    val brush = linearGradient(
        colors,
        start = Offset(xShimmer - gradientWidth, yShimmer - gradientWidth),
        end = Offset(xShimmer, yShimmer)
    )
    Column(
        modifier = Modifier
            .padding(padding)
            .alpha(alpha)
    ) {
        Surface(
            shape = cardShape,
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(cardHeight)
                    .background(brush = brush)
            )
        }
    }
}


inline fun <reified T : ViewModel> assistedViewModel(
    arguments: Bundle? = null,
    owner: SavedStateRegistryOwner,
    crossinline viewModelProducer: (SavedStateHandle) -> T,
) =
    object : AbstractSavedStateViewModelFactory(owner, arguments) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ) =
            viewModelProducer(handle) as T
    }

@Composable
fun rememberBooleanSaveableDefaultFalse() = rememberSaveable { mutableStateOf(false) }

@Composable
fun rememberStringSaveableDefaultEmpty() = rememberSaveable { mutableStateOf("") }

@Composable
inline fun loadPicture(
    url: String,
    requestOptions: RequestOptions.() -> Unit = {}
): StateFlow<GlideImageState> {
    val target = remember { GlideFlowTarget() }
    GlideApp.with(LocalContext.current)
        .applyDefaultRequestOptions(RequestOptions().also { it.requestOptions() })
        .asBitmap()
        .load(url)
        .into(target)

    return target.imageState
}