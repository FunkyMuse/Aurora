package com.funkymuse.aurora.bottomnavigation.destinations

import androidx.annotation.StringRes

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
sealed class BottomNavScreen(val route: String, @StringRes val resourceID: Int) {

    companion object {
        const val SEARCH = "search"
        const val FAVORITES = "favorites"
        const val LATEST_BOOKS = "latestBooks"
        const val SETTINGS = "settings"
    }


}