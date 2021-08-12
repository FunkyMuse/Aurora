package com.funkymuse.aurora.downloadsui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.funkymuse.composed.core.context
import com.funkymuse.style.shape.Shapes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */

@Composable
fun CopyFileDialog(uri: Uri, filePath: File, onDone: () -> Unit) {
    val localContext = context
    var progress by remember { mutableStateOf(0f) }
    var openDialog by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    localContext.moveHere(uri, filePath, dismiss = {
        openDialog = false
    }) {
        scope.launch {
            delay(2500)
            progress = it.toFloat()
        }
    }

    if (openDialog){

    } else {
        onDone()
    }
    Dialog(
        onDismissRequest = {
            openDialog = false
        }, properties =
        DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colors.background, shape = Shapes.medium)
        ) {
            Text(
                text = stringResource(id = R.string.copying_file),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "%${progress.toInt()}",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }

}


private fun Context.moveHere(
    treeUri: Uri,
    file: File,
    dismiss: () -> Unit = {},
    updateProgressText: (progress: Long) -> Unit
) {
    try {
        contentResolver.openOutputStream(treeUri)?.use { output ->
            output as FileOutputStream
            FileInputStream(file).use { input ->
                output.channel.truncate(0)
                copyStream(file.length(), input, output) {
                    updateProgressText(it)
                    if (it == 100L) {
                        dismiss()
                    }
                }
            }
        }
    } catch (it: Throwable) {
        it.printStackTrace()
        dismiss()
    }
}

private inline fun copyStream(
    size: Long,
    inputStream: InputStream,
    os: OutputStream,
    bufferSize: Int = 4096,
    progress: (Long) -> Unit = {}
) {
    try {
        val bytes = ByteArray(bufferSize)
        var count = 0
        var prog = 0
        while (count != -1) {
            count = inputStream.read(bytes)
            if (count != -1) {
                os.write(bytes, 0, count)
                prog += count
                progress(prog.toLong() * 100 / size)
            }
        }
        os.flush()
        inputStream.close()
        os.close()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}