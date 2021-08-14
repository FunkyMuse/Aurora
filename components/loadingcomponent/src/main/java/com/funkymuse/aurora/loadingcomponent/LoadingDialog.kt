package com.funkymuse.aurora.loadingcomponent

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */

@Composable
fun LoadingDialog(@StringRes title:Int = R.string.loading) {
    Dialog(
        onDismissRequest = {}, properties =
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        LoadingBubbles(title)
    }
}