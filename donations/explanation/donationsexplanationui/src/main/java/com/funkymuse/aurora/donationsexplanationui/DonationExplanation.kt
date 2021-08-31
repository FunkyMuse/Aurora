package com.funkymuse.aurora.donationsexplanationui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.confirmationdialog.ConfirmationDialog
import com.funkymuse.aurora.donationsdestination.DonateDestination
import com.funkymuse.aurora.navigator.AuroraNavigatorViewModel

/**
 * Created by funkymuse on 8/17/21 to long live and prosper !
 */

@Composable
fun DonationsExplanation() {
    val viewModel: AuroraNavigatorViewModel = hiltViewModel()
    ConfirmationDialog(title = stringResource(id = R.string.donate_explanation), confirmText = stringResource(
        id = R.string.support
    ), onDismiss = {
        viewModel.navigateUp()
    }) {
        viewModel.navigate(DonateDestination.route())
    }
}