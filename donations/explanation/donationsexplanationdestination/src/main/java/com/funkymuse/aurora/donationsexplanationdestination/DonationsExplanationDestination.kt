package com.funkymuse.aurora.donationsexplanationdestination

import com.funkymuse.aurora.navigator.NavigationDestination

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */
const val DONATE_PREFS_KEY = "explain_prefs_key"
object DonationsExplanationDestination : NavigationDestination {
    private const val DONATE_EXPLANATION_DESTINATION = "explain_donations"
    override fun route(): String = DONATE_EXPLANATION_DESTINATION
}