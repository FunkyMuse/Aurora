package com.funkymuse.aurora.composeextensions

import com.funkymuse.aurora.onetimepreferences.OneTimePreferencesViewModel
import com.funkymuse.aurora.runcodeeveryxlaunch.RunCodePreferencesViewModel

/**
 * Created by funkymuse on 8/17/21 to long live and prosper !
 */
interface AssistedHiltInjectibles {
    val runCodePreferencesViewModelFactory: RunCodePreferencesViewModel.RunCodePreferencesViewModelFactory
    val oneTimePreferencesViewModelFactory: OneTimePreferencesViewModel.OneTimePreferencesViewModelFactory
}