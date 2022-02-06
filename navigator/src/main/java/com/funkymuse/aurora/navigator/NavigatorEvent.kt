package com.funkymuse.aurora.navigator

import androidx.navigation.NavOptionsBuilder

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
sealed class NavigatorEvent {
    object NavigateUp : NavigatorEvent()
    class Directions(
        val destination: String,
        val builder: NavOptionsBuilder.() -> Unit
    ) : NavigatorEvent()

    object PopBackStack : NavigatorEvent()
}