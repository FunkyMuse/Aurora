package com.funkymuse.aurora.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.funkymuse.aurora.topappbars.TopAppBarWithBackButton

/**
 * Created by funkymuse on 8/15/21 to long live and prosper !
 */

@Composable
fun ScaffoldWithBack(
    modifier: Modifier  = Modifier,
    onBackClicked: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithBackButton(onBackClicked = onBackClicked)
        }) {
        content(it)
    }
}

