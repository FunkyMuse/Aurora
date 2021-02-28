package com.funkymuse.aurora.extensions

import androidx.compose.ui.graphics.ImageBitmap

sealed class GlideImageState {
    object Loading : GlideImageState()
    data class Success(val imageBitmap: ImageBitmap?) : GlideImageState()
    data class Failure(val errorDrawable: ImageBitmap?) : GlideImageState()
}