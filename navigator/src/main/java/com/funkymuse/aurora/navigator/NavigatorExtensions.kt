package com.funkymuse.aurora.navigator

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.NavDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument

private const val HIDE_BOTTOM_NAV_ARG = "com.funkymuse.aurora.navigator.HIDE_BOTTOM_NAV_ARG"

val hideBottomNamedArgument: NamedNavArgument
    get() = navArgument(HIDE_BOTTOM_NAV_ARG) {
        type = NavType.BoolType
        defaultValue = true
    }

val NavDestination?.hideBottomNavigation
    get() = hideBottomNamedArgument.argument == this?.arguments?.get(
        HIDE_BOTTOM_NAV_ARG
    )
val hideBottomBundleArgument: NavArgument get() = hideBottomNamedArgument.argument