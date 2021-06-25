package com.funkymuse.aurora.bottomnavigation.destinations

import com.funkymuse.aurora.R
import com.funkymuse.aurora.navigator.NavigatorEvent

/**
 * Created by funkymuse on 6/23/21 to long live and prosper !
 */
object SearchBottomNavRoute : BottomNavScreen(SEARCH, R.string.search) {

    val destination = NavigatorEvent.Directions { SEARCH }
}