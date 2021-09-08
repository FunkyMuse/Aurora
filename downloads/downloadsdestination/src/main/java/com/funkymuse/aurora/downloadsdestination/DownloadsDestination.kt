package com.funkymuse.aurora.downloadsdestination

import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink
import com.funkymuse.aurora.navigator.NavigationDestination

/**
 * Created by funkymuse on 8/14/21 to long live and prosper !
 */
const val DOWNLOADS = "downloads"

object DownloadsDestination : NavigationDestination {

    override fun route(): String = DOWNLOADS

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(BOOK_ID_PARAM) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        }
    )
    override val deepLinks: List<NavDeepLink> = listOf(navDeepLink {
        uriPattern = DOWNLOADED_BOOK_NAME_URI_PATTERN
    })
}