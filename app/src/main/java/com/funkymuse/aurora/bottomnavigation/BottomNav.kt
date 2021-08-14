package com.funkymuse.aurora.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bottomnavigation.destinations.*
import com.funkymuse.aurora.crashesdestination.CrashesDestination
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination

/**
 * Created by funkymuse on 3/29/21 to long live and prosper !
 */
object BottomNav {

    val bottomNavigationEntries =
            listOf(
                    BottomEntry(
                            SearchBottomNavRoute,
                            Icons.Filled.Search
                    ),
                    BottomEntry(
                            DownloadsBottomNavRoute,
                            Icons.Filled.Download
                    ),
                    BottomEntry(
                            FavoritesBottomNavRoute,
                            Icons.Filled.Favorite
                    ),
                    BottomEntry(
                            LatestBooksBottomNavRoute,
                            Icons.Filled.List
                    ),
                    BottomEntry(
                            SettingsBottomNavRoute,
                            Icons.Filled.Settings
                    ),
            )

    val hideBottomNavOnDestinations = listOf(
            BookDetailsDestination.route(),
            SearchResultDestination.route(),
            CrashesDestination.route()
    )

}