package com.funkymuse.aurora.bookdetailsui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.crazylegend.intent.openWebPage
import com.crazylegend.retrofit.apiresult.ApiResult
import com.crazylegend.retrofit.apiresult.onApiError
import com.crazylegend.retrofit.apiresult.onError
import com.crazylegend.retrofit.apiresult.onLoading
import com.crazylegend.retrofit.apiresult.onSuccess
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.crazylegend.string.clearHtmlTags
import com.crazylegend.string.isNotNullOrEmpty
import com.funkymuse.aurora.backbuttoncomponent.BackButton
import com.funkymuse.aurora.bookdetailsdata.BookDetailsViewModel
import com.funkymuse.aurora.bookdetailsdata.VPNWarningModel
import com.funkymuse.aurora.commonextensions.hasVPN
import com.funkymuse.aurora.confirmationdialog.ConfirmationDialog
import com.funkymuse.aurora.errorcomponent.ErrorMessage
import com.funkymuse.aurora.errorcomponent.ErrorWithRetry
import com.funkymuse.aurora.favoritebookmodel.FavoriteBook
import com.funkymuse.aurora.internetdetector.InternetDetectorViewModel
import com.funkymuse.aurora.loadingcomponent.LoadingBubbles
import com.funkymuse.aurora.loadingcomponent.LoadingDialog
import com.funkymuse.aurora.scrapermodel.ScraperResult
import com.funkymuse.aurora.serverconstants.LIBGEN_COVER_IMAGE_URL
import com.funkymuse.aurora.serverconstants.LIBGEN_LC
import com.funkymuse.aurora.serverconstants.LIBRARY_LOL
import com.funkymuse.aurora.serverconstants.mirrorsUrls
import com.funkymuse.aurora.serverconstants.torrentDownloadURL
import com.funkymuse.aurora.settingsdata.SettingsViewModel
import com.funkymuse.bookdetails.bookdetailsmodel.DetailedBookModel
import com.funkymuse.composed.core.collectAndRemember
import com.funkymuse.composed.core.context
import com.funkymuse.composed.core.stateWhenStarted
import com.funkymuse.style.shape.Shapes

/**
 * Created by FunkyMuse, date 2/27/21
 */


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DetailedBook() {
    val bookDetailsViewModel: BookDetailsViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val internetDetectorViewModel: InternetDetectorViewModel = hiltViewModel()
    val onBackClicked = {
        bookDetailsViewModel.navigateUp()
    }
    val book by stateWhenStarted(flow = bookDetailsViewModel.book, initial = ApiResult.Loading)
    val localContext = context
    val favoritesBook by bookDetailsViewModel.favoriteBook.collectAndRemember(initial = null)
    var detailedBook by remember { mutableStateOf<DetailedBookModel?>(null) }
    val isVPNWarningEnabled by settingsViewModel.vpnWarning.collectAndRemember(false)


    val showLoadingDialog by
    bookDetailsViewModel.extractLink.collectAndRemember(initial = ScraperResult.Idle)
    if (showLoadingDialog is ScraperResult.Loading) {
        LoadingDialog(R.string.preparing)
    }

    val vpnWarningModel by bookDetailsViewModel.showVPNWarning.collectAsStateWithLifecycle(VPNWarningModel.Idle)
    if (vpnWarningModel is VPNWarningModel.DownloadBook) {
        ShowNotOnVpnDialog(onDismiss = {
            bookDetailsViewModel.dismissVPNWarning()
        }, onConfirm = {
            val model = vpnWarningModel as VPNWarningModel.DownloadBook
            bookDetailsViewModel.downloadBook(
                model.id,
                model.extension,
                model.title
            )
        })
    }


    val retry = {
        bookDetailsViewModel.retry()
    }
    ScaffoldWithBackAndFavorites(
        book is ApiResult.Success,
        favoritesBook,
        onBackClicked = {
            onBackClicked()
        },
        onFavoritesClicked = {
            favoritesClick(favoritesBook, detailedBook, bookDetailsViewModel)
        }
    ) {
        book
            .onLoading { LoadingBubbles() }
            .onApiError { _, _ ->
                ErrorWithRetry(R.string.no_book_loaded) {
                    retry()
                }
            }
            .onError { throwable ->
                if (throwable is NoConnectionException) {
                    if (internetDetectorViewModel.collectAsStateWithLifecycle( false).value) {
                        retry()
                    }
                    ErrorMessage(R.string.no_book_loaded_no_connect)
                } else {
                    ErrorWithRetry(R.string.no_book_loaded) {
                        retry()
                    }
                }
            }
            .onSuccess { bookList ->
                detailedBook = bookList.firstOrNull()
                if (detailedBook == null) {
                    onBackClicked()
                    return@onSuccess
                }
                detailedBook?.apply {
                    DetailedBook(this) { bookId ->
                        if (!localContext.hasVPN() && isVPNWarningEnabled) {
                            bookDetailsViewModel.showNotOnVPN(
                                bookId,
                                extension.toString(),
                                title.toString()
                            )
                        } else {
                            bookDetailsViewModel.downloadBook(
                                bookId,
                                extension.toString(),
                                title.toString()
                            )
                        }
                    }
                }
            }
    }
}

@Composable
fun ShowNotOnVpnDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ConfirmationDialog(
        stringResource(id = R.string.not_on_vpn_download_warning),
        confirmText = stringResource(id = R.string.continue_dl),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

private fun favoritesClick(
    favoritesBook: FavoriteBook?,
    detailedBook: DetailedBookModel?,
    bookDetailsViewModel: BookDetailsViewModel
) {
    if (favoritesBook == null) {
        detailedBook?.let { bookModel ->
            bookDetailsViewModel.addToFavorites(
                FavoriteBook(
                    id = bookModel.md5?.lowercase()?.trim() ?: bookDetailsViewModel.id.lowercase()
                        .trim(),
                    title = bookModel.title,
                    realImage = bookModel.coverurl,
                    author = bookModel.author,
                    extension = bookModel.extension?.uppercase(),
                    pages = bookModel.pagesInFile,
                    favoriteSize = bookModel.fileSize,
                    year = bookModel.year
                )
            )
        }
    } else {
        bookDetailsViewModel.removeFromFavorites(favoritesBook.id)
    }
}

@Composable
fun ScaffoldWithBackAndFavorites(
    showFavoritesButton: Boolean,
    favoritesBook: FavoriteBook?,
    onBackClicked: () -> Unit,
    onFavoritesClicked: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarBookDetails(onBackClicked, favoritesBook != null, showFavoritesButton) {
                onFavoritesClicked()
            }
        }) {
        content(it)
    }
}


@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4_XL, name = "Book")
@Composable
fun BookPreview() {
    DetailedBook(book = DetailedBookModel.testBook) {}
}


@Composable
fun DetailedBook(
    book: DetailedBookModel,
    onDownloadLinkClicked: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val imageUrl = LIBGEN_COVER_IMAGE_URL + book.coverurl
    val localContext = context
    val painter = rememberAsyncImagePainter(imageUrl)

    val dlMirrors = book.md5?.let { mirrorsUrls(it) }?.associateWith {
        val hasDirectDownload = it.contains(LIBGEN_LC, true) || it.contains(LIBRARY_LOL, true)
        hasDirectDownload
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val alignment = Modifier.align(Alignment.Start)
        val imageModifier = Modifier
            .size(width = 200.dp, height = 240.dp)
            .padding(top = 18.dp)

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = imageModifier
                .clip(RectangleShape),
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            text = book.author ?: stringResource(id = R.string.not_available),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp, textAlign = TextAlign.Center, color = Color.Gray,
            )
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = book.title ?: stringResource(id = R.string.not_available),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp, textAlign = TextAlign.Center
            )
        )

        book.descr?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.description_detail,
                    clearHtmlTags()
                )
        }

        book.year?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.year,
                    this
                )
        }

        book.language?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.language_detail,
                    this
                )
        }


        book.pages?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.number_of_pages_detail,
                    this
                )
        }


        book.extension?.uppercase()?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.file_type_detail,
                    this
                )
        }

        book.timeadded?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.time_added_detail,
                    this
                )
        }

        book.timelastmodified?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.time_last_modified_detail,
                    this
                )
        }

        book.edition?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.edition_detail,
                    this
                )
        }

        book.publisher?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.publisher_detail,
                    this
                )
        }

        book.series?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.series_detail,
                    this
                )
        }

        book.volumeinfo?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.volume_info_detail,
                    this
                )
        }

        book.periodical?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.periodical_detail,
                    this
                )
        }

        if (!dlMirrors?.values.isNullOrEmpty()) {
            var menuExpanded by remember { mutableStateOf(false) }

            DetailedButton(stringResource(id = R.string.download_mirrors)) {
                menuExpanded = true
            }
            DropdownMenu(expanded = menuExpanded,
                modifier = Modifier.fillMaxWidth(),
                offset = DpOffset(32.dp, 16.dp),
                onDismissRequest = { menuExpanded = false }) {
                dlMirrors?.forEach {
                    DropdownMenuItem(onClick = {
                        if (it.value) {
                            onDownloadLinkClicked(it.key)
                        } else {
                            localContext.openWebPage(it.key)
                        }
                        menuExpanded = false
                    }) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = stringResource(id = R.string.download)
                        )

                        Text(
                            text = stringResource(id = if (it.value) R.string.direct_download else R.string.web_download),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        if (book.md5.isNotNullOrEmpty()) {
            DetailedButton(stringResource(id = R.string.torrent_download)) {
                localContext.openWebPage(torrentDownloadURL(book.md5.toString()))
            }
            Spacer(modifier = Modifier.padding(top = 16.dp, bottom = 76.dp))
        } else {
            Spacer(modifier = Modifier.padding(66.dp))
        }

    }
}

@Composable
fun TopAppBarBookDetails(
    onBackClicked: () -> Unit, isInFavorites: Boolean, showFavoritesButton: Boolean,
    onFavoritesClicked: () -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier.statusBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackButton(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp), onClick = onBackClicked
            )

            if (showFavoritesButton) {
                AddToFavorites(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp), isInFavorites, onFavoritesClicked
                )
            }
        }
    }
}

@Composable
@Preview
fun AddToFavorites(
    modifier: Modifier = Modifier,
    isInFavorites: Boolean = false,
    onClicked: () -> Unit = {}
) {
    context
    val favoritesIndicator =
        if (isInFavorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
    Button(
        onClick = onClicked,
        shape = Shapes.large,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
        modifier = modifier
    ) {
        Icon(
            imageVector = favoritesIndicator,
            contentDescription = stringResource(id = R.string.title_favorites)
        )
    }
}

@Composable
fun TitleCardWithContent(modifier: Modifier = Modifier, title: Int, text: String) {
    Card(
        elevation = 2.dp,
        shape = Shapes.medium,
        modifier = modifier
            .padding(start = 22.dp)
            .offset(y = 16.dp)
            .zIndex(2f),
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = title),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = Shapes.large
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
                text = text,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
@Preview
fun DetailedButton(text: String = "Some button text", onClicked: () -> Unit = {}) {
    Button(
        shape = Shapes.large, onClick = onClicked,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(text = text)
    }
}