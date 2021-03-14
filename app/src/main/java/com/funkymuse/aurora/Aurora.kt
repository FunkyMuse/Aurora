package com.funkymuse.aurora

import android.app.Application
import com.crazylegend.kotlinextensions.misc.disableNightMode
import com.crazylegend.kotlinextensions.misc.enableNightMode
import com.funkymuse.aurora.settings.DefaultPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class Aurora : Application()