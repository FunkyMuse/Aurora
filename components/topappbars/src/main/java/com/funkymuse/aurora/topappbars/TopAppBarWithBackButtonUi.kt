package com.funkymuse.aurora.topappbars

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.funkymuse.aurora.backbuttoncomponent.BackButton

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */

@Composable
fun TopAppBarWithBackButton(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    content: @Composable (BoxScope) -> Unit = {}
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        modifier = modifier.statusBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackButton(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp), onClick = onBackClicked
            )
            content(this)
        }
    }
}