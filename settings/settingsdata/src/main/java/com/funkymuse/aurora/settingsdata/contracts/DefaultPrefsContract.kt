package com.funkymuse.aurora.settingsdata.contracts

import kotlinx.coroutines.flow.Flow

/**
 * Created by funkymuse on 3/14/21 to long live and prosper !
 */
interface DefaultPrefsContract {
    val darkTheme: Flow<Boolean>
    val vpnWarning: Flow<Boolean>

    companion object {
        const val DARK_THEME_KEY = "dark_theme"
        const val VPN_WARNING_KEY = "vpn_warning"
    }

}

