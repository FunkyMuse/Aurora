package com.funkymuse.aurora.settingsdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by funkymuse on 3/14/21 to long live and prosper !
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
        private val defaultPreferences: DefaultPreferences,
) : ViewModel() {

    val darkTheme = defaultPreferences.darkTheme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun changeTheme(isDarkThemeEnabled: Boolean) {
        viewModelScope.launch { defaultPreferences.changeTheme(isDarkThemeEnabled) }
    }
}