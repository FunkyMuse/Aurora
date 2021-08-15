package com.funkymuse.aurora.donatedata

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crazylegend.common.exhaustive
import com.crazylegend.intent.openWebPage
import com.crazylegend.toaster.Toaster
import com.funkymuse.aurora.common.copyToClipboard
import com.funkymuse.aurora.common.decodeBase64
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */
@HiltViewModel
class DonationsViewModel @Inject constructor(
    private val toaster: Toaster,
    application: Application
) : AndroidViewModel(application) {

    private val context get() = getApplication<Application>() as Context

    private companion object {
        //prevent bots from crawling raw data
        private const val btcAddress = "MTk5RDNTVW11M0QzY3h3dWdORDRkYXprQXV3emRpU0JEOQ=="
        private const val ethAddress = "MHgyMTQ5OTc3NGJjMGJERUY3MmRlNDg1M0YzZDZhQzAzNDgyMDk3ZjU5"
        private const val githubAddress = "aHR0cHM6Ly9naXRodWIuY29tL3Nwb25zb3JzL0Z1bmt5TXVzZS8="
        private const val patreonAddress = "aHR0cHM6Ly93d3cucGF0cmVvbi5jb20vZnVua3ltdXNl"
    }

    private fun copyToClipboard(stringText: String, toastRes: Int) {
        context.copyToClipboard(stringText)
        toaster.shortToast(toastRes)
        viewModelScope.launch {
            delay(500)
            toaster.shortToast(R.string.thank_you_for_the_support)
        }
    }

    fun onItemClick(item: DonationModel) {
        when (item.donationType) {
            DonationModel.DonationType.BTC -> {
                copyToClipboard(btcAddress.decodeBase64, R.string.bitcoin_address_copied_to_clipboard)
            }
            DonationModel.DonationType.ETH -> {
                copyToClipboard(ethAddress.decodeBase64, R.string.ethereum_address_copied_to_clipboard)
            }
            DonationModel.DonationType.PATREON -> {
                val patreon = patreonAddress.decodeBase64
                context.openWebPage(patreon) {
                    unableToOpenPage(patreon, R.string.patreon_url_copied_to_clipboard)
                }
            }
            DonationModel.DonationType.GITHUB -> {
                val github = githubAddress.decodeBase64
                context.openWebPage(github) {
                    unableToOpenPage(github, R.string.github_url_copied_to_clipboard)
                }
            }
        }.exhaustive
    }
    private fun unableToOpenPage(url: String, @StringRes copiedToClipboardRes : Int) {
        context.copyToClipboard(url)
        toaster.shortToast(copiedToClipboardRes)
    }


}