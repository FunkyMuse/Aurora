package com.funkymuse.aurora.settingsdata

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.funkymuse.aurora.settingsdata.contracts.DefaultPrefsContract
import com.funkymuse.aurora.settingsdata.contracts.DefaultPrefsContract.Companion.DARK_THEME_KEY
import com.funkymuse.aurora.settingsdata.contracts.DefaultPrefsContract.Companion.VPN_WARNING_KEY
import com.funkymuse.aurora.settingsdata.contracts.DefaultPrefsStateChangeContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 3/14/21 to long live and prosper !
 */
@Singleton
class DefaultPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) :
    DefaultPrefsContract, DefaultPrefsStateChangeContract {

    private val darkThemeKey = booleanPreferencesKey(DARK_THEME_KEY)
    private val vpnWarningKey = booleanPreferencesKey(VPN_WARNING_KEY)

    //region vpn
    override val vpnWarning: Flow<Boolean> =
        dataStore.data.map { it[vpnWarningKey] ?: true }

    override suspend fun changeVPNWarning(isVPNWarningEnabled: Boolean) {
        dataStore.edit { it[vpnWarningKey] = isVPNWarningEnabled }
    }
    //endregion

    //region dark theme
    override val darkTheme = dataStore.data.map { it[darkThemeKey] ?: false }

    override suspend fun changeTheme(isDarkThemeEnabled: Boolean) {
        dataStore.edit { it[darkThemeKey] = isDarkThemeEnabled }
    }
    //endregion
}