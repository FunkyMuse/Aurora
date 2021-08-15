package com.funkymuse.aurora.donateui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.navigator.NavigatorViewModel
import com.funkymuse.aurora.scaffolds.ScaffoldWithBack

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */

@Composable
fun Donations() {
    val navigator = hiltViewModel<NavigatorViewModel>()
    ScaffoldWithBack(onBackClicked = { navigator.navigateUp() }) {

    }
}