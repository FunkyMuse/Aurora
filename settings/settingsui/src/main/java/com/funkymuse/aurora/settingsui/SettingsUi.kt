package com.funkymuse.aurora.settingsui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.funkymuse.aurora.crashesdestination.CrashesDestination
import com.funkymuse.aurora.donationsdestination.DonateDestination
import com.funkymuse.aurora.extensions.openWebPage
import com.funkymuse.aurora.navigator.AuroraNavigatorViewModel
import com.funkymuse.aurora.settingsdata.MY_OTHER_APPS
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.composed.core.context
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Settings() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val navigator: AuroraNavigatorViewModel = hiltViewModel()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.asPaddingValues()

    ) {
        item { DarkTheme(viewModel.darkTheme) { viewModel.changeTheme(it) } }
        item { VPNWarning(viewModel.vpnWarning) { viewModel.changeVPNWarning(it) } }
        item { DonateSettings { navigator.navigate(DonateDestination.route()) } }
        item { CrashesSettings { navigator.navigate(CrashesDestination.route()) } }
        item { MyOtherApps() }
        item { VersionNumber() }
        item { License() }
    }
}

@Composable
fun License() {
    TitleWithSubtitleTextItem(
        titleText = stringResource(id = R.string.license_title),
        subtitleText = stringResource(id = R.string.license)
    )
}

@Composable
private fun TitleWithSubtitleTextItem(titleText: String, subtitleText: String) {
    SettingsItem(
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = titleText, modifier =
                Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = subtitleText,
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
    TitleWithSubtitleTextItem(
        titleText = stringResource(id = R.string.version),
        subtitleText = context.getVersionName()
    )
}

@Composable
fun CrashesSettings(navigateToCrashes: () -> Unit) {
    SettingsItem(modifier = Modifier
        .clickable {
            navigateToCrashes()
        }
        .padding(vertical = 8.dp)) {
        Text(
            text = stringResource(id = R.string.crashes), modifier =
            Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun DonateSettings(navigateToDonations: () -> Unit) {

    SettingsItem(modifier = Modifier
            .clickable {
                navigateToDonations()
            }
            .padding(vertical = 8.dp)) {
        Text(
            text = stringResource(id = R.string.donate), modifier =
            Modifier.padding(horizontal = 8.dp)
        )
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
        Text(
            text = stringResource(id = R.string.my_other_apps),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
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

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DarkTheme(
    darkThemeFlow: StateFlow<Boolean>,
    changeTheme: (theme: Boolean) -> Unit
) {
    val darkTheme by darkThemeFlow.collectAsStateWithLifecycle()
    Switch(R.string.dark_theme, changeTheme, darkTheme)
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun VPNWarning(
    booleanFlow: StateFlow<Boolean>,
    booleanAction: (Boolean) -> Unit
) {
    val condition by booleanFlow.collectAsStateWithLifecycle()
    Switch(R.string.do_not_show_warning_vpn, booleanAction, condition)
}



@Composable
private fun Switch(
    @StringRes text: Int,
    callBack: (condition: Boolean) -> Unit,
    condition: Boolean
) {
    SettingsItem(modifier = Modifier
            .clickable {
                callBack(!condition)
            }
            .padding(top = 8.dp)) {
        CheckBoxWithText(text = text,
            isChecked = condition,
            checkChanged = {
                callBack(it)
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
    Box(
        modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
    ) {
        Text(
            text = stringResource(id = text),
            modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp, end = 4.dp),
            textAlign = TextAlign.Start
        )

        Switch(
            checked = isChecked, onCheckedChange = checkChanged,
            modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(start = 8.dp, end = 4.dp)
        )
    }
}