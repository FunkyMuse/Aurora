package com.funkymuse.aurora.crashesui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crazylegend.crashyreporter.CrashyReporter
import com.funkymuse.aurora.backbuttoncomponent.BackButton
import com.funkymuse.aurora.extensions.openWebPage
import com.funkymuse.aurora.navigator.NavigatorViewModel
import com.funkymuse.aurora.toaster.ToasterViewModel
import com.google.accompanist.insets.statusBarsPadding

/**
 * Created by funkymuse on 6/29/21 to long live and prosper !
 */

@ExperimentalMaterialApi
@Composable
fun Crashes() {
    val navigator = hiltViewModel<NavigatorViewModel>()
    val toaster = hiltViewModel<ToasterViewModel>()
    val crashes = remember {
        CrashyReporter.getLogsAsStrings()
    }
    val showToast = {
        toaster.shortToast(R.string.crash_copied_to_clipboard)
    }

    if (crashes.isNullOrEmpty()) {
        toaster.shortToast(R.string.no_crashes)
        navigator.navigateUp()
        return
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarBookDetails {
                navigator.navigateUp()
            }
        }) {
        LazyColumn {
            itemsIndexed(crashes) { index, item ->
                CrashItem(index + 1, item, showToast)
            }
        }
    }
}

const val CRASHES_URL = "https://github.com/FunkyMuse/Aurora/issues/new"

@ExperimentalMaterialApi
@Composable
fun CrashItem(index: Int, item: String, showToast: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp)
        .padding(top = 18.dp)
        .wrapContentHeight(), onClick = {
        clipboardManager.apply {
            clipboardManager.setText(AnnotatedString(text = item))
            showToast()
            context.openWebPage(CRASHES_URL)
        }
    }) {
        Text(
            text = stringResource(id = R.string.crash_number, index),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun TopAppBarBookDetails(
    onBackClicked: () -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier.statusBarsPadding()
    ) {
        BackButton(modifier = Modifier.padding(8.dp), onClick = onBackClicked)
    }
}