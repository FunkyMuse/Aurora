package com.funkymuse.aurora.downloadsui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.confirmationdialog.ConfirmationDialog
import com.funkymuse.aurora.downloadsdata.CreateFileContract
import com.funkymuse.aurora.downloadsdata.DownloadsModel
import com.funkymuse.aurora.downloadsdata.DownloadsViewModel
import com.funkymuse.aurora.downloadsdata.FileModel
import com.funkymuse.aurora.errorcomponent.ErrorMessage
import com.funkymuse.aurora.toaster.ToasterViewModel
import com.funkymuse.composed.core.collectAndRemember
import com.funkymuse.composed.core.context
import com.funkymuse.composed.core.lazylist.lastVisibleIndexState
import com.funkymuse.style.shape.Shapes
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */

const val DEFAULT_MIME_TYPE = "application/pdf"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Downloads() {
    val downloadsViewModel = hiltViewModel<DownloadsViewModel>()
    val toasterViewModel = hiltViewModel<ToasterViewModel>()
    val downloadsModel by downloadsViewModel.files.collectAndRemember(DownloadsModel.Loading)
    val progressVisibility by derivedStateOf { downloadsModel is DownloadsModel.Loading }
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()
    val swipeToRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    val localContext = context
    val highlightBook by downloadsViewModel.highlightDownloadedBook.collectAsState(initial = null)

    val retry = {
        downloadsViewModel.retry()
    }

    var clickedModel by remember { mutableStateOf<FileModel?>(null) }
    var longClickedModel by remember { mutableStateOf<FileModel?>(null) }
    var uri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(contract = CreateFileContract(),
        onResult = {
            uri = it
            if (it == null) {
                toasterViewModel.shortToast(R.string.operation_cancelled)
            }
        })

    longClickedModel?.apply {
        DeleteDownload(it = this, onDismiss = { longClickedModel = null }) {
            toasterViewModel.shortToast(R.string.deleted)
        }
    }

    clickedModel?.let { fileModel ->
        uri?.let {
            CopyFileDialog(uri = it, filePath = fileModel.file) {
                longClickedModel = fileModel
                clickedModel = null
                uri = null
                toasterViewModel.shortToast(R.string.copying_file_success)
            }
        }
    }


    val onBookClicked = { fileModel: FileModel ->
        toasterViewModel.longToast(R.string.copying_file_elsewhere)
        clickedModel = fileModel
        val mimeType = fileModel.getMimeType(localContext)
        launcher.launch(Pair(mimeType ?: DEFAULT_MIME_TYPE, fileModel.fileNameAndExtension))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = progressVisibility, modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentSize()
                .systemBarsPadding()
                .padding(top = 8.dp)
                .zIndex(2f)) {
            CircularProgressIndicator()
        }


        val lastVisibleIndexState by columnState.lastVisibleIndexState()

        val isButtonVisible = lastVisibleIndexState?.let { it > 20 } ?: false

        AnimatedVisibility(visible = isButtonVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(start = false, end = false)
                .padding(bottom = 64.dp)
                .zIndex(2f)) {

            Box {
                FloatingActionButton(
                    modifier = Modifier.padding(5.dp),
                    onClick = { scope.launch { columnState.scrollToItem(0) } },
                ) {
                    Icon(
                        Icons.Filled.ArrowUpward,
                        contentDescription = stringResource(id = R.string.go_back_to_top),
                        tint = Color.White
                    )
                }
            }
        }

        SwipeRefresh(
            state = swipeToRefreshState, onRefresh = {
                swipeToRefreshState.isRefreshing = true
                retry()
                swipeToRefreshState.isRefreshing = false
            },
            modifier = Modifier.fillMaxSize()
        ) {
            if (downloadsModel is DownloadsModel.Success) {
                val list = (downloadsModel as DownloadsModel.Success).filesModel
                LazyColumn(
                    state = columnState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 56.dp, top = 8.dp),
                    contentPadding = rememberInsetsPaddingValues(
                        insets = LocalWindowInsets.current.systemBars,
                        additionalBottom = 36.dp
                    )
                ) {
                    items(list, itemContent = { item ->
                        DownloadedBookItem(
                            item,
                            highlightBook == item.bookId,
                            onBookClicked = onBookClicked,
                            onLongBookClick = {
                                longClickedModel = it
                            })
                    })
                }

            } else {
                ErrorMessage(R.string.no_downloads)
            }
        }

    }
}

@Composable
@Preview
fun PreviewDownloadedItem() {
    DownloadedBookItem(

        fileModel = FileModel(
            "Femine fiction: Revisiting the Postmodern",
            125123L,
            "PDF",
            "asdfajo234adsf",
            File(context.filesDir, "")
        ),
        true
    ) {

    }
}

fun Modifier.blink(highlightBook: Boolean): Modifier = composed {
    if (highlightBook) {
        val alphaAnimation = remember { Animatable(0f) }
        LaunchedEffect(alphaAnimation) {
            alphaAnimation.animateTo(
                1f, repeatable(
                    3, tween(500, 80)
                )
            )
        }
        graphicsLayer(alpha = alphaAnimation.value)
    } else {
        this
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadedBookItem(
    fileModel: FileModel,
    highlightBook: Boolean = false,
    onLongBookClick: (FileModel) -> Unit = {},
    onBookClicked: (FileModel) -> Unit
) {

    Card(
        shape = Shapes.large,
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .blink(highlightBook)
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .combinedClickable(onLongClick = {
                    onLongBookClick(fileModel)
                }, onClick = {
                    onBookClicked(fileModel)

                })
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = fileModel.fileName,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = fileModel.size,
                    style = TextStyle(fontWeight = FontWeight.Light, fontSize = 17.sp),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .padding(bottom = 14.dp),
                )

                Text(
                    text = fileModel.extension.uppercase(),
                    style = TextStyle(fontWeight = FontWeight.Light, fontSize = 17.sp),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .padding(bottom = 14.dp),
                )
            }
        }
    }
}

@Composable
fun DeleteDownload(
    it: FileModel,
    onDismiss: () -> Unit,
    onDeleted: () -> Unit
) {
    ConfirmationDialog(
        title = stringResource(
            R.string.delete_downloads,
            it.fileName
        ), onDismiss = onDismiss, onConfirm = {
            it.file.delete()
            onDeleted()
        }, confirmText = stringResource(id = R.string.delete)
    )
}