package com.funkymuse.aurora.navigator

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
@Singleton
internal class NavigatorImpl @Inject constructor() : Navigator {

    private val navigationEvents = Channel<NavigatorEvent>()
    override val destinations = navigationEvents.receiveAsFlow()

    override fun navigateUp(): Boolean =
        navigationEvents.trySend(NavigatorEvent.NavigateUp).isSuccess

    override fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit): Boolean =
        navigationEvents.trySend(NavigatorEvent.Directions(route, builder)).isSuccess

}