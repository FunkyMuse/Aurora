package com.funkymuse.aurora.navigator

import kotlinx.coroutines.flow.SharedFlow

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
interface Navigator {

    fun navigateUp(): Boolean
    fun navigate(directions: NavigationDestination): Boolean
    val destinations: SharedFlow<NavigatorEvent?>
}