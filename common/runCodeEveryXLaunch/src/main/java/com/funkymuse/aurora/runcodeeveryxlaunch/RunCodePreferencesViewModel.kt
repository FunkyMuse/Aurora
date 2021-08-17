package com.funkymuse.aurora.runcodeeveryxlaunch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.commonextensions.context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by funkymuse on 8/17/21 to long live and prosper !
 */

const val ASSISTED_SETTINGS_NAME = "settings_name"
const val LAUNCHES_UNTIL_RUN_NAME = "launches_name"

class RunCodePreferencesViewModel @AssistedInject constructor(
    application: Application,
    @Assisted(ASSISTED_SETTINGS_NAME) private val SETTINGS_NAME: String,
    @Assisted(LAUNCHES_UNTIL_RUN_NAME) private val LAUNCHES_UNTIL_RUN: Int
) : AndroidViewModel(application) {

    @AssistedFactory
    interface RunCodePreferencesViewModelFactory {
        fun create(
            @Assisted(ASSISTED_SETTINGS_NAME) SETTINGS_NAME: String,
            @Assisted(LAUNCHES_UNTIL_RUN_NAME) LAUNCHES_UNTIL_RUN: Int,
        ): RunCodePreferencesViewModel
    }

    private companion object {
        private const val launchCountPref = "launchCount"
        private const val dateFirstLaunchPref = "dateFirLaunched"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)
    private val countPrefKey = longPreferencesKey(launchCountPref)
    private val dateFirstLaunchKey = longPreferencesKey(dateFirstLaunchPref)

    private val codeToRunChannel = Channel<Boolean>(Channel.BUFFERED)
    val runCode = codeToRunChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        runCode()
    }

    private fun runCode() {
        viewModelScope.launch {
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
                if (launchCount >= LAUNCHES_UNTIL_RUN) {
                    if (System.currentTimeMillis() >= dateFirstLaunch) {
                        codeToRunChannel.send(true)
                        //reset launch count and date of first launch
                        it[countPrefKey] = 0
                        it[dateFirstLaunchKey] = 0
                    }
                }
            }
        }
    }
}