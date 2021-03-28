package com.funkymuse.aurora.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import com.funkymuse.aurora.bookDetails.BOOK_DETAILS_BOTTOM_NAV_ROUTE
import com.funkymuse.aurora.searchResult.SEARCH_ROUTE_BOTTOM_NAV

/**
 * Created by funkymuse on 3/29/21 to long live and prosper !
 */
object BottomNav {

    val bottomNavigationEntries =
        listOf(
            BottomEntry(
                BottomNavScreen.Search,
                Icons.Filled.Search
            ),
            BottomEntry(
                BottomNavScreen.Favorites,
                Icons.Filled.Favorite
            ),
            BottomEntry(
                BottomNavScreen.LatestBooks,
                Icons.Filled.List
            ),
            BottomEntry(
                BottomNavScreen.Settings,
                Icons.Filled.Settings
            ),
        )

    val hideBottomNavOnDestinations = listOf(
        BOOK_DETAILS_BOTTOM_NAV_ROUTE,
        SEARCH_ROUTE_BOTTOM_NAV,
    )

}