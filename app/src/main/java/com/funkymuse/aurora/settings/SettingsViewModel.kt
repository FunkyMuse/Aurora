package com.funkymuse.aurora.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by funkymuse on 3/14/21 to long live and prosper !
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(private val defaultPreferences: DefaultPreferences) : ViewModel(), DefaultPrefsContract by defaultPreferences {

}