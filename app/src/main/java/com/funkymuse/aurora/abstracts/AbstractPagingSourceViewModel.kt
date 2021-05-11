package com.funkymuse.aurora.abstracts

import android.app.Application
import androidx.lifecycle.SavedStateHandle

/**
 * Created by Hristijan, date 3/5/21
 */
abstract class AbstractPagingSourceViewModel(
    application: Application,
    val savedStateHandle: SavedStateHandle
) : AbstractPagingViewModel(application)