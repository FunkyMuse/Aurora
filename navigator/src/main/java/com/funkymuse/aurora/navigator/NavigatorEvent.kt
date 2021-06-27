package com.funkymuse.aurora.navigator

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
sealed class NavigatorEvent {
    object NavigateUp : NavigatorEvent()
    class Directions(val destination: NavigationDestination) : NavigatorEvent()
}