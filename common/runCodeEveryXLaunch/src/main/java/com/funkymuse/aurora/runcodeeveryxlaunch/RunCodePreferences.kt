package com.funkymuse.aurora.runcodeeveryxlaunch

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Created by funkymuse on 3/14/21 to long live and prosper !
 */

const val ASSISTED_SETTINGS_NAME = "settings_name"

class RunCodePreferences @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted(ASSISTED_SETTINGS_NAME) private val SETTINGS_NAME: String
) {

    @AssistedFactory
    interface RunCodePreferencesFactory {
        fun create(@Assisted(ASSISTED_SETTINGS_NAME) SETTINGS_NAME: String): RunCodePreferences
    }

    private companion object {
        private const val launchCountPref = "launchCount"
        private const val dateFirstLaunchPref = "dateFirLaunched"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)
    private val countPrefKey = longPreferencesKey(launchCountPref)
    private val dateFirstLaunchKey = longPreferencesKey(dateFirstLaunchPref)

    suspend fun runCode(
        launchesUntilRun: Int,
        codeToRun: () -> Unit
    ) {

        context.dataStore.edit {
            // Increment launch counter
            val launchCount = it[countPrefKey] ?: 0
            it[countPrefKey] = launchCount + 1


            // Get date of first launch
            var dateFirstLaunch = it[dateFirstLaunchKey] ?: 0

            if (dateFirstLaunch == 0L) {
                dateFirstLaunch = System.currentTimeMillis()
                it[dateFirstLaunchKey] = dateFirstLaunch
            }

            // Wait at least n days before opening
            if (launchCount >= launchesUntilRun) {
                if (System.currentTimeMillis() >= dateFirstLaunch) {
                    codeToRun()
                    //reset launch count and date of first launch
                    it[countPrefKey] = 0
                    it[dateFirstLaunchKey] = 0
                }
            }
        }
    }

}