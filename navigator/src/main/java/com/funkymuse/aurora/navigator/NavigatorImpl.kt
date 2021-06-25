package com.funkymuse.aurora.navigator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
@Singleton
internal class NavigatorImpl @Inject constructor() : Navigator {

    private val navigationEvents = MutableStateFlow<NavigatorEvent?>(null)
    override val destinations = navigationEvents.asStateFlow()

    override fun navigateUp() {
        navigationEvents.value = NavigatorEvent.NavigateUp
    }

    override fun navigate(directions: NavigationDestination) {
        navigationEvents.value = NavigatorEvent.Directions(directions)
    }
}