package com.funkymuse.aurora.extensions

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.funkymuse.aurora.R

/**
 * Created by Hristijan, date 2/28/21
 */
@GlideModule
class AuroraGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.ic_logo)
        )
    }
}