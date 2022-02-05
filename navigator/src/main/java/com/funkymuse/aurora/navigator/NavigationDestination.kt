package com.funkymuse.aurora.navigator

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink

@OptIn(ExperimentalAnimationApi::class)
fun interface NavigationDestination {
    fun route(): String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val deepLinks: List<NavDeepLink>
        get() = emptyList()

    val enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = null

    val exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() = null

    val popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = enterTransition

    val popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() = exitTransition

    val dialogProperties: DialogProperties
        get() = DialogProperties()
}