package com.funkymuse.aurora.radiobutton

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * Created by funkymuse on 6/27/21 to long live and prosper !
 */

@Composable
fun RadioButtonWithText(
        @StringRes text: Int,
        isChecked: Boolean,
        onRadioButtonClicked: () -> Unit
) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        RadioButton(
                selected = isChecked,
                onClick = onRadioButtonClicked,
                modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
                text = stringResource(id = text), modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
                .clickable { onRadioButtonClicked() }
        )
    }
}

@Composable
fun RadioButtonWithTextNotClickable(
        @StringRes text: Int,
        isChecked: Boolean,
) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Image(
                imageVector = if (isChecked) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                modifier = Modifier.align(Alignment.CenterVertically),
                contentDescription = null
        )
        Text(
                text = stringResource(id = text), modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
        )
    }
}
