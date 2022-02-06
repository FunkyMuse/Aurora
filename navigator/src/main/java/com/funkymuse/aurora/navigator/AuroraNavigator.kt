package com.funkymuse.aurora.navigator

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
interface AuroraNavigator {

    fun navigateUp(): Boolean
    fun popBackStack()
    fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }): Boolean
    val destinations: Flow<NavigatorEvent>
}