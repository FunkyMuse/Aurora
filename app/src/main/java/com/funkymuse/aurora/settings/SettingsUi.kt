package com.funkymuse.aurora.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import com.funkymuse.composed.core.stateWhenStarted
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import kotlinx.coroutines.launch

@Composable
fun Settings() {
    LazyColumn(modifier = Modifier.fillMaxSize(),
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues()) {
        item { DarkTheme() }
    }
}


@Composable
fun SettingsItem(item: @Composable (BoxScope) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        item(this)
    }
}

@Composable
fun DarkTheme() {
    val viewModel = hiltNavGraphViewModel<SettingsViewModel>()
    val darkTheme by stateWhenStarted(flow = viewModel.darkTheme, initial = false)
    val scope = rememberCoroutineScope()
    SettingsItem {
        CheckBoxWithText(text = com.funkymuse.aurora.R.string.dark_theme,
            isChecked = darkTheme,
            checkChanged = {
                scope.launch { viewModel.changeTheme(it) }
            })
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsItemPreview() {
    CheckBoxWithText(text = com.funkymuse.aurora.R.string.dark_theme,
        isChecked = false,
        checkChanged = {})
}

@Composable
fun CheckBoxWithText(
    @StringRes text: Int,
    isChecked: Boolean,
    checkChanged: (Boolean) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        val (textWidget, checkboxWidget) = createRefs()
        Text(
            text = stringResource(id = text),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(textWidget) {
                    start.linkTo(parent.start)
                    end.linkTo(checkboxWidget.start)
                    width = Dimension.fillToConstraints
                    centerVerticallyTo(parent)
                }
                .padding(start = 8.dp, end = 4.dp),
            textAlign = TextAlign.Start
        )

        Switch(
            checked = isChecked, onCheckedChange = checkChanged,
            modifier = Modifier
                .constrainAs(checkboxWidget) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end)
                }
                .padding(start = 8.dp, end = 4.dp)
        )
    }
}