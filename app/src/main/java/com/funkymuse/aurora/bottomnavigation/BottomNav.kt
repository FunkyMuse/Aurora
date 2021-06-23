package com.funkymuse.aurora.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import com.funkymuse.aurora.bookDetails.BOOK_DETAILS_BOTTOM_NAV_ROUTE
import com.funkymuse.aurora.bottomnavigation.destinations.FavoritesBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.LatestBooksBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SearchBottomNavRoute
import com.funkymuse.aurora.bottomnavigation.destinations.SettingsBottomNavRoute
import com.funkymuse.aurora.searchResult.SEARCH_ROUTE_BOTTOM_NAV

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
        BOOK_DETAILS_BOTTOM_NAV_ROUTE,
        SEARCH_ROUTE_BOTTOM_NAV,
    )

}