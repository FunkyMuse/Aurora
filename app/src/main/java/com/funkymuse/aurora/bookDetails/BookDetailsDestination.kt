package com.funkymuse.aurora.bookDetails

import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.funkymuse.aurora.navigator.NavigationDestination

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
object BookDetailsDestination {

    val destination = object : NavigationDestination {
        override fun route(): String = BOOK_DETAILS_BOTTOM_NAV_ROUTE

        override val arguments: List<NamedNavArgument>
            get() = listOf(navArgument(BOOK_ID_PARAM) { type = NavType.IntType })
    }

    fun bookDetailsRoute(bookID: Int) = NavigationDestination { "$BOOK_DETAILS_ROUTE/${bookID}" }
}