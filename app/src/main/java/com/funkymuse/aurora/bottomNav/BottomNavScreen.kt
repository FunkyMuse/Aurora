package com.funkymuse.aurora.bottomNav

import androidx.annotation.StringRes
import com.funkymuse.aurora.R
/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
sealed class BottomNavScreen(val route: String, @StringRes val resourceID: Int) {
    companion object {
        private const val SEARCH = "search"
        private const val FAVORITES = "favorites"
        private const val LATEST_BOOKS = "latest"
        private const val SETTINGS = "settings"
    }

    object Search : BottomNavScreen(SEARCH, R.string.search)
    object Favorites : BottomNavScreen(FAVORITES, R.string.title_favorites)
    object LatestBooks : BottomNavScreen(LATEST_BOOKS, R.string.title_latest)
    object Settings : BottomNavScreen(SETTINGS, R.string.title_settings)
}