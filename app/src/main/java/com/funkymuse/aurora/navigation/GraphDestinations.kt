package com.funkymuse.aurora.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.funkymuse.aurora.bookdetailsdestination.BookDetailsDestination
import com.funkymuse.aurora.bookdetailsui.DetailedBook
import com.funkymuse.aurora.crashesdestination.CrashesDestination
import com.funkymuse.aurora.crashesui.Crashes
import com.funkymuse.aurora.donatedestination.DonateDestination
import com.funkymuse.aurora.donateui.Donations
import com.funkymuse.aurora.downloadsdestination.DownloadsDestination
import com.funkymuse.aurora.downloadsui.Downloads
import com.funkymuse.aurora.navigator.NavigationDestination
import com.funkymuse.aurora.searchresultdestination.SearchResultDestination
import com.funkymuse.aurora.searchresultui.SearchResult

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */

@OptIn(ExperimentalMaterialApi::class)
private val destinations: Map<NavigationDestination, @Composable () -> Unit> = mapOf(
    DonateDestination to { Donations() },
    DownloadsDestination to { Downloads() },
    CrashesDestination to { Crashes() },
    SearchResultDestination to { SearchResult() },
    BookDetailsDestination to { DetailedBook() },
)


fun NavGraphBuilder.addDestinations() {
    destinations.forEach { entry ->
        val destination = entry.key
        composable(destination.route(), destination.arguments, destination.deepLinks) {
            entry.value()
        }
    }
}