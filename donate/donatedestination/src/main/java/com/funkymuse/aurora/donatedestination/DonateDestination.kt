package com.funkymuse.aurora.donatedestination

import com.funkymuse.aurora.navigator.NavigationDestination

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */
object DonateDestination : NavigationDestination {
    private const val DONATE_DESTINATION = "donate"
    override fun route(): String = DONATE_DESTINATION
}