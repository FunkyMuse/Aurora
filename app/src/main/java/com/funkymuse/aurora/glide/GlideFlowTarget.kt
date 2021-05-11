package com.funkymuse.aurora.glide

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by FunkyMuse, date 2/28/21
 */
 class GlideFlowTarget : CustomTarget<Bitmap>() {

    private val glideState = MutableStateFlow<GlideImageState>(GlideImageState.Loading)
    val imageState = glideState.asStateFlow()

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
        glideState.value = GlideImageState.Success(resource.asImageBitmap())
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        val bitmap = (errorDrawable as? BitmapDrawable)?.bitmap
        glideState.value = GlideImageState.Failure(bitmap?.asImageBitmap())
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        val bitmap = (placeholder as? BitmapDrawable)?.bitmap
        glideState.value = GlideImageState.Success(bitmap?.asImageBitmap())
    }
}

