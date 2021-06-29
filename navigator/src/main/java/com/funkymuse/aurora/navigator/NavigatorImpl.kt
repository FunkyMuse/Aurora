package com.funkymuse.aurora.navigator

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
@Singleton
internal class NavigatorImpl @Inject constructor() : Navigator {

    /**
     * Adding new subscribers to this is O(1) which
     * should happen only once in an Activity and emitting has O(n) cost which ideally
     * if you've subscribed only in the Activity it's O(1) still.
     */
    private val navigationEvents = MutableSharedFlow<NavigatorEvent?>(replay = 1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val destinations = navigationEvents.asSharedFlow()

    override fun navigateUp(): Boolean = navigationEvents.tryEmit(NavigatorEvent.NavigateUp)
    override fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit): Boolean = navigationEvents.tryEmit(NavigatorEvent.Directions(route, builder))

}