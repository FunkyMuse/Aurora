package com.funkymuse.aurora.settingsdata.contracts

/**
 * Created by funkymuse on 8/17/21 to long live and prosper !
 */
interface DefaultPrefsStateChangeContract {
    suspend fun changeTheme(isDarkThemeEnabled: Boolean)
    suspend fun changeVPNWarning(isVPNWarningEnabled: Boolean)
}