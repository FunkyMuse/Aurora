package com.funkymuse.aurora.onetimepreferences

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.commonextensions.context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by funkymuse on 8/17/21 to long live and prosper !
 */
private const val SETTINGS_PREF_NAME = "one_time_pref_name"
private const val ONE_TIME_KEY = "bool_one_time"
class OneTimePreferencesViewModel @AssistedInject constructor(
    @Assisted(SETTINGS_PREF_NAME) private val name :String,
    application:Application
) : AndroidViewModel(application) {

    @AssistedFactory
    interface OneTimePreferencesViewModelFactory {
        fun create(
            @Assisted(SETTINGS_PREF_NAME)name :String,
        ):OneTimePreferencesViewModel
    }

    private val booleanKey = booleanPreferencesKey(ONE_TIME_KEY)
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = name)

    val isEventFired: StateFlow<Boolean> =
        context.dataStore.data.map { it[booleanKey] ?: false }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun setEventIsFired(){
        setEventCase(true)
    }

    fun setEventIsNotFired(){
        setEventCase(false)
    }

    private fun setEventCase(condition: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit {
                it[booleanKey] = condition
            }
        }
    }
}