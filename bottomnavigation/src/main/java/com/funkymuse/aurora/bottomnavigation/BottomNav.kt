package com.funkymuse.aurora.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.funkymuse.aurora.downloadsdestination.DOWNLOADS

/**
 * Created by funkymuse on 3/29/21 to long live and prosper !
 */

object SearchRoute : BottomNavigationEntry(SEARCH, R.string.search)
object DownloadsRoute : BottomNavigationEntry(DOWNLOADS, R.string.downloads)
object FavoritesRoute : BottomNavigationEntry(FAVORITES, R.string.title_favorites)
object LatestBooksRoute : BottomNavigationEntry(LATEST_BOOKS, R.string.title_latest)
object SettingsRoute : BottomNavigationEntry(SETTINGS, R.string.title_settings)

val bottomNavigationEntries =
    listOf(
        BottomNavigationUiEntry(
            SearchRoute,
            Icons.Filled.Search
        ),
        BottomNavigationUiEntry(
            DownloadsRoute,
            Icons.Filled.Download
        ),
        BottomNavigationUiEntry(
            FavoritesRoute,
            Icons.Filled.Favorite
        ),
        BottomNavigationUiEntry(
            LatestBooksRoute,
            Icons.Filled.List
        ),
        BottomNavigationUiEntry(
            SettingsRoute,
            Icons.Filled.Settings
        ),
    )