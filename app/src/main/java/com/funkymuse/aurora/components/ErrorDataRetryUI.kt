package com.funkymuse.aurora.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funkymuse.aurora.R
import com.funkymuse.aurora.ui.theme.Shapes

/**
 * Created by Hristijan, date 3/3/21
 */

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowErrorDataWithRetry(text: String = "", onRetryClick:()->Unit = {}) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = Shapes.large,
            modifier = Modifier.padding(20.dp)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.no_latest_books),
                    modifier = Modifier
                        .padding(16.dp),
                    fontSize = 24.sp
                )

                Text(
                    text = stringResource(id = R.string.retry),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            onRetryClick()
                        }
                        .padding(bottom = 16.dp),
                    contentDescription = stringResource(id = R.string.retry),
                    imageVector = Icons.Filled.Replay
                )
            }
        }
    }
}