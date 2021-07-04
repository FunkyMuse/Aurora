package com.funkymuse.aurora.settingsui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.crashesdestination.CrashesDestination
import com.funkymuse.aurora.extensions.openWebPage
import com.funkymuse.aurora.navigator.NavigatorViewModel
import com.funkymuse.aurora.settingsdata.MY_OTHER_APPS
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.composed.core.context
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Settings() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val navigator: NavigatorViewModel = hiltViewModel()

    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars
            )
    ) {
        item { DarkTheme(viewModel.darkTheme) { viewModel.changeTheme(it) } }
        item { CrashesSettings { navigator.navigate(CrashesDestination.route()) } }
        item { MyOtherApps() }
        item { VersionNumber() }
        item { License() }
    }
}

@Composable
fun License() {
    TitleWithSubtitleTextItem(titleText = stringResource(id = R.string.license_title),
            subtitleText = stringResource(id = R.string.license))
}

@Composable
private fun TitleWithSubtitleTextItem(titleText: String, subtitleText: String) {
    SettingsItem(modifier = Modifier
            .padding(vertical = 8.dp)) {
        Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()) {
            Text(text = titleText, modifier =
            Modifier.padding(horizontal = 8.dp))

            Text(text = subtitleText,
                    modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(top = 4.dp),
                    fontSize = 12.sp, color = Color.Gray
            )

        }
    }
}

fun Context.getVersionName(): String = packageManager.getPackageInfo(packageName, 0).versionName


@Composable
fun VersionNumber() {
    TitleWithSubtitleTextItem(titleText = stringResource(id = R.string.version),
            subtitleText = context.getVersionName())
}

@Composable
fun CrashesSettings(navigateToCrashes: () -> Unit) {

    SettingsItem(modifier = Modifier
            .clickable {
                navigateToCrashes()
            }
            .padding(vertical = 8.dp)) {
        Text(text = stringResource(id = R.string.crashes), modifier =
        Modifier.padding(horizontal = 8.dp))
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
        darkThemeFlow: StateFlow<Boolean>,
        changeTheme: (theme: Boolean) -> Unit
) {
    val darkTheme = darkThemeFlow.collectAsState().value
    SettingsItem(modifier = Modifier
            .clickable {
                changeTheme(!darkTheme)
            }
            .padding(top = 8.dp)) {
        CheckBoxWithText(text = R.string.dark_theme,
                isChecked = darkTheme,
                checkChanged = {
                    changeTheme(it)
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