package com.funkymuse.aurora.crashesdestination

import com.funkymuse.aurora.navigator.NavigationDestination

/**
 * Created by funkymuse on 6/29/21 to long live and prosper !
 */
object CrashesDestination : NavigationDestination {

    override fun route(): String = CRASH_ROUTE

    private const val CRASH_ROUTE = "crashes"

}