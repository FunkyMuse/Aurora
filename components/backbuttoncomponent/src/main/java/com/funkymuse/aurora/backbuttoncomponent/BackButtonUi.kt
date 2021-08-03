package com.funkymuse.aurora.backbuttoncomponent

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.funkymuse.style.shape.Shapes

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Preview
@Composable
fun PreviewBackButton() {
    BackButton()
}

@Composable
fun BackButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}, ) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
        modifier = modifier,
        shape = Shapes.large
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(id = R.string.back)
        )
    }
}