package com.funkymuse.aurora.extensions

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.bumptech.glide.request.RequestOptions
import com.funkymuse.aurora.R
import com.funkymuse.composed.core.context
import com.funkymuse.composed.core.density
import com.funkymuse.composed.core.savedStateRegistryOwner
import com.funkymuse.composed.core.stateWhenStarted
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues



@PublishedApi
internal inline fun <reified T : ViewModel> createAssistedViewModel(
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
inline fun <reified T : ViewModel> assistedViewModel(
    arguments: Bundle? = null,
    crossinline viewModelProducer: (SavedStateHandle) -> T,
): T =
    viewModel(factory = createAssistedViewModel(
        arguments = arguments,
        owner = savedStateRegistryOwner
    ) {
        viewModelProducer(it)
    })


@Composable
inline fun loadPicture(
    url: String,
    requestOptions: RequestOptions.() -> Unit = {}
): GlideImageState {
    val target = remember { GlideFlowTarget() }
    GlideApp.with(context)
        .applyDefaultRequestOptions(RequestOptions().also { it.requestOptions() })
        .asBitmap()
        .load(url)
        .into(target)

    val state by stateWhenStarted(flow = target.imageState, initial = GlideImageState.Loading)
    return state
}

