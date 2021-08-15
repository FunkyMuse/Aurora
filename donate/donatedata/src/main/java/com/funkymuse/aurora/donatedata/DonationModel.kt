package com.funkymuse.aurora.donatedata

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */
data class DonationModel(@StringRes val title: Int, @DrawableRes val drawable: Int, val donationType: DonationType) {

    enum class DonationType {
        BTC, ETH, PATREON, GITHUB
    }
}