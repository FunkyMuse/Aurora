package com.funkymuse.aurora.bookdetailsdestination

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.funkymuse.aurora.navigator.NavigationDestination
import com.funkymuse.aurora.navigator.hideBottomNamedArgument

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
object BookDetailsDestination : NavigationDestination {

    override fun route(): String = BOOK_DETAILS_BOTTOM_NAV_ROUTE

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(BOOK_ID_PARAM) { type = NavType.StringType },
            hideBottomNamedArgument
        )

    const val BOOK_ID_PARAM = "book"

    private const val BOOK_DETAILS_ROUTE = "book_details"
    private const val BOOK_DETAILS_BOTTOM_NAV_ROUTE = "$BOOK_DETAILS_ROUTE/{$BOOK_ID_PARAM}"
    fun createBookDetailsRoute(bookID: String) = "$BOOK_DETAILS_ROUTE/${bookID.lowercase()}"

}