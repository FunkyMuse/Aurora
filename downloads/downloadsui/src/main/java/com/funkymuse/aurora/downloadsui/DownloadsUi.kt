package com.funkymuse.aurora.downloadsui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.funkymuse.aurora.downloadsdata.CreateFileContract
import com.funkymuse.aurora.downloadsdata.DownloadsModel
import com.funkymuse.aurora.downloadsdata.DownloadsViewModel
import com.funkymuse.aurora.downloadsdata.FileModel
import com.funkymuse.aurora.errorcomponent.ErrorMessage
import com.funkymuse.composed.core.context
import com.funkymuse.composed.core.lazylist.lastVisibleIndexState
import com.funkymuse.composed.core.rememberBooleanDefaultFalse
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DownloadsUi() {
    val downloadsViewModel: DownloadsViewModel = hiltViewModel()
    var progressVisibility by rememberBooleanDefaultFalse()
    val downloadsModel = downloadsViewModel.files.collectAsState().value
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()
    val swipeToRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    val ctx = context

    var clickedModel by remember { mutableStateOf<File?>(null) }
    var uri by remember { mutableStateOf<Uri?>(null) }
    progressVisibility = downloadsModel is DownloadsModel.Loading

    val launcher = rememberLauncherForActivityResult(contract = CreateFileContract(),
        onResult = {
            uri = it
        })

    clickedModel?.let { file ->
        uri?.let {
            CopyFileDialog(uri = it, filePath = file) {
                clickedModel = null
                uri = null
            }
        }
    }
    val retry = {
        downloadsViewModel.retry()
    }

    val onBookClicked = { fileModel: FileModel ->
        clickedModel = fileModel.file
        val mimeType = fileModel.getMimeType(ctx)
        launcher.launch(Pair(mimeType ?: "application/pdf", fileModel.fileNameAndExtension))
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (loading, backToTop) = createRefs()
        AnimatedVisibility(
            visible = progressVisibility, modifier = Modifier
                .constrainAs(loading) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
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
                .constrainAs(backToTop) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent)
                }
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
                val list = downloadsModel.filesModel
                LazyColumn(
                    state = columnState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 56.dp, top = 8.dp),
                    contentPadding = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.systemBars)
                ) {
                    items(list, itemContent = { item ->
                        DownloadedBookItem(item, onBookClicked)
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
        )
    ) {

    }
}

@Composable
fun DownloadedBookItem(
    fileModel: FileModel,
    onBookClicked: (FileModel) -> Unit
) {
    Card(
        shape = Shapes.large,
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onBookClicked(fileModel)
            }
    ) {
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
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
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp),
                )

                Text(
                    text = fileModel.extension.uppercase(),
                    style = TextStyle(fontWeight = FontWeight.Light, fontSize = 17.sp),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp),
                )
            }
        }
    }
}
