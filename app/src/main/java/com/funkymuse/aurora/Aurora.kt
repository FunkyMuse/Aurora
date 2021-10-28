package com.funkymuse.aurora

import android.app.Application
import android.util.Log.INFO
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.funkymuse.aurora.appscope.ApplicationScope
import com.funkymuse.aurora.settingsdata.DefaultPreferences
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class Aurora : Application(), Configuration.Provider {



    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(INFO)
            .setWorkerFactory(workerFactory)
            .build()

    @Inject
    lateinit var defaultPreferences: DefaultPreferences

    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applyDarkTheme()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    private fun applyDarkTheme() {
        appScope.launch {
            defaultPreferences.darkTheme.firstOrNull()?.let { isDarkThemeEnabled ->
                if (isDarkThemeEnabled) enableNightMode() else disableNightMode()
            }
        }
    }

    private fun disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
    }

    private fun enableNightMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)

    }
}