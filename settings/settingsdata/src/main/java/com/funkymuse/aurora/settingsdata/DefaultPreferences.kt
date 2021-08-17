package com.funkymuse.aurora.settingsdata

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.funkymuse.aurora.settingsdata.DefaultPrefsContract.Companion.DARK_THEME_KEY
import com.funkymuse.aurora.settingsdata.DefaultPrefsContract.Companion.VPN_WARNING_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 3/14/21 to long live and prosper !
 */
@Singleton
class DefaultPreferences @Inject constructor(@ApplicationContext private val context: Context) :
    DefaultPrefsContract {

    private companion object {
        private const val SETTINGS_NAME = "settings"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)

    private val darkThemeKey = booleanPreferencesKey(DARK_THEME_KEY)
    private val vpnWarningKey = booleanPreferencesKey(VPN_WARNING_KEY)

    //region vpn
    override val vpnWarning: Flow<Boolean> =
        context.dataStore.data.map { it[vpnWarningKey] ?: true }

    override suspend fun changeVPNWarning(isVPNWarningEnabled: Boolean) {
        context.dataStore.edit { it[vpnWarningKey] = isVPNWarningEnabled }
    }
    //endregion

    //region dark theme
    override val darkTheme = context.dataStore.data.map { it[darkThemeKey] ?: false }

    override suspend fun changeTheme(isDarkThemeEnabled: Boolean) {
        context.dataStore.edit { it[darkThemeKey] = isDarkThemeEnabled }
    }
    //endregion
}