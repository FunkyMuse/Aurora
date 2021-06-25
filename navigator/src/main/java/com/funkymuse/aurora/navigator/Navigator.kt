package com.funkymuse.aurora.navigator

import kotlinx.coroutines.flow.StateFlow

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
interface Navigator {

    fun navigateUp()
    fun navigate(directions: NavigationDestination)
    val destinations: StateFlow<NavigatorEvent?>
}