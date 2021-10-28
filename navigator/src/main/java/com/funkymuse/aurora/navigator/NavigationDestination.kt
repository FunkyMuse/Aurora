package com.funkymuse.aurora.navigator

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
fun interface NavigationDestination {

    fun route(): String
    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val deepLinks: List<NavDeepLink>
        get() = emptyList()
}