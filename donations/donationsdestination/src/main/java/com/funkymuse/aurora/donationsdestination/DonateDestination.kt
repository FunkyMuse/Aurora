package com.funkymuse.aurora.donationsdestination

import androidx.navigation.NamedNavArgument
import com.funkymuse.aurora.navigator.NavigationDestination
import com.funkymuse.aurora.navigator.hideBottomNamedArgument

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */
object DonateDestination : NavigationDestination {
    private const val DONATE_DESTINATION = "donate"
    override fun route(): String = DONATE_DESTINATION
    override val arguments: List<NamedNavArgument>
        get() = listOf(hideBottomNamedArgument)
}