package com.funkymuse.aurora.donationsui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.composeextensions.assistedInjectable
import com.funkymuse.aurora.donationsdata.DonationModel
import com.funkymuse.aurora.donationsdata.DonationsViewModel
import com.funkymuse.aurora.navigator.NavigatorViewModel
import com.funkymuse.aurora.scaffolds.ScaffoldWithBack

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */

private val adapterList by lazy {
    listOf(
        DonationModel(
            R.string.bitcoin,
            R.drawable.btc,
            DonationModel.DonationType.BTC
        ),
        DonationModel(
            R.string.ethereum,
            R.drawable.eth,
            DonationModel.DonationType.ETH
        ),
        DonationModel(
            R.string.github,
            R.drawable.github,
            DonationModel.DonationType.GITHUB
        ),
        DonationModel(
            R.string.patreon,
            R.drawable.patreon,
            DonationModel.DonationType.PATREON
        ),
    )
}

const val USER_DONATED_KEY = "userHasDonated"

@Composable
fun Donations() {
    val navigator = hiltViewModel<NavigatorViewModel>()
    val viewModel = hiltViewModel<DonationsViewModel>()

    val oneTimePreferencesViewModel = assistedInjectable(produce = {
        oneTimePreferencesViewModelFactory.create(USER_DONATED_KEY)
    })

    ScaffoldWithBack(onBackClicked = { navigator.navigateUp() }) {
        LazyColumn {
            items(adapterList) { item ->
                DonationItem(donationResId = item.title, drawableRes = item.drawable) {
                    viewModel.onItemClick(item)
                    oneTimePreferencesViewModel.setEventIsFired()
                }
            }
        }
    }
}


@Composable
fun DonationItem(
    @StringRes donationResId: Int,
    @DrawableRes drawableRes: Int,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 18.dp)
            .wrapContentHeight()
            .clickable { onItemClick() }
    ) {
        Row {
            Image(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 16.dp),
                painter = painterResource(id = drawableRes),
                contentDescription = null
            )
            Text(
                text = stringResource(id = donationResId),
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(16.dp)
            )
        }
    }
}


@Composable
@Preview
fun DonationItemPreview(
) {
    DonationItem(R.string.github, R.drawable.github) {}
}