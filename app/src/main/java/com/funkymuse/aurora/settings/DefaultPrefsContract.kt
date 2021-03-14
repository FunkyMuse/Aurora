package com.funkymuse.aurora.settings

import kotlinx.coroutines.flow.Flow

/**
 * Created by funkymuse on 3/14/21 to long live and prosper !
 */
interface DefaultPrefsContract {
    val darkTheme: Flow<Boolean>
    suspend fun changeTheme(isDarkThemeEnabled: Boolean)
}