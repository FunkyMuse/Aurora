package com.funkymuse.aurora.crashesdestination

import androidx.navigation.NamedNavArgument
import com.funkymuse.aurora.navigator.NavigationDestination
import com.funkymuse.aurora.navigator.hideBottomNamedArgument

/**
 * Created by funkymuse on 6/29/21 to long live and prosper !
 */
object CrashesDestination : NavigationDestination {

    override fun route(): String = CRASH_ROUTE

    private const val CRASH_ROUTE = "crashes"

    override val arguments: List<NamedNavArgument>
        get() = listOf(hideBottomNamedArgument)

}