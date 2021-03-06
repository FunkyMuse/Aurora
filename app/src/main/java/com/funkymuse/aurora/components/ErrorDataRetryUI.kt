package com.funkymuse.aurora.components

import androidx.annotation.RawRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.funkymuse.aurora.R
import com.funkymuse.aurora.bookDetails.TopAppBarBackOnly

/**
 * Created by Hristijan, date 3/3/21
 */

@Composable
fun ScaffoldLottieWithBack(
    showRetry: Boolean = false,
    onRetryClicked: () -> Unit = {},
    onBackClicked: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBarBackOnly(onBackClicked)
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /*LottieAnim(
                modifier = Modifier.align(Alignment.CenterHorizontally), anim = anim
            )*/

            if (showRetry) {
                RetryOption(onRetryClicked)
            }
        }
    }
}

@Composable
@Preview
fun LottieWithRetry(
    @RawRes anim: Int = R.raw.server_error,
    showRetry: Boolean = false,
    onRetryClicked: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        /*LottieAnim(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(36.dp), anim = anim
        )*/
        if (showRetry) {
            RetryOption(onRetryClicked)
        }
    }
}

@Composable
@Preview
fun RetryOption(onRetryClicked: () -> Unit = {}) {
    Column(modifier = Modifier.clickable {
        onRetryClicked()
    }) {
        Icon(
            imageVector = Icons.Filled.Replay,
            contentDescription = stringResource(id = R.string.retry),
            modifier = Modifier
                .size(50.dp)
        )
        Text(text = stringResource(id = R.string.retry), modifier = Modifier.padding(8.dp))
    }
}
