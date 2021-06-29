package com.funkymuse.aurora.settingsui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.settingsdata.MY_OTHER_APPS
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.composed.core.stateWhenStarted
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlinx.coroutines.launch

@Composable
fun Settings() {
    val viewModel: SettingsViewModel = hiltViewModel()

    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars
            )
    ) {
        item { DarkTheme(viewModel) }
        item { MyOtherApps() }
    }
}

@Composable
fun MyOtherApps() {
    val context = LocalContext.current
    SettingsItem(modifier = Modifier
            .clickable {
                context.openWebPage(MY_OTHER_APPS)
            }
            .padding(vertical = 8.dp)) {
        Text(text = stringResource(id = R.string.my_other_apps),
                modifier = Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
fun SettingsItem(modifier: Modifier = Modifier, item: @Composable (BoxScope) -> Unit) {
    Box(
            modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
    ) {
        item(this)
    }
}

@Composable
fun DarkTheme(
        viewModel: SettingsViewModel
) {
    val darkTheme by stateWhenStarted(flow = viewModel.darkTheme, initial = false)
    val scope = rememberCoroutineScope()
    SettingsItem(modifier = Modifier.clickable {
        scope.launch { viewModel.changeTheme(!darkTheme) }
    }) {
        CheckBoxWithText(text = R.string.dark_theme,
                isChecked = darkTheme,
                checkChanged = {
                    scope.launch { viewModel.changeTheme(it) }
                })
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsItemPreview() {
    CheckBoxWithText(text = R.string.dark_theme,
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