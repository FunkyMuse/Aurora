package com.funkymuse.aurora.bookdetailsui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.crazylegend.collections.isNotNullOrEmpty
import com.crazylegend.intent.openWebPage
import com.crazylegend.retrofit.retrofitResult.RetrofitResult
import com.crazylegend.retrofit.retrofitResult.handle
import com.crazylegend.retrofit.retryOnConnectedToInternet
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.crazylegend.string.clearHtmlTags
import com.crazylegend.string.isNotNullOrEmpty
import com.funkymuse.aurora.bookdetailsdata.BookDetailsViewModel
import com.funkymuse.aurora.bookdetailsmodel.DetailedBookModel
import com.funkymuse.aurora.components.BackButton
import com.funkymuse.aurora.components.ErrorMessage
import com.funkymuse.aurora.components.ErrorWithRetry
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.favoritebookdb.FavoriteBook
import com.funkymuse.aurora.internetdetector.InternetDetectorViewModel
import com.funkymuse.aurora.loading.CardShimmer
import com.funkymuse.aurora.loading.LoadingBubbles
import com.funkymuse.aurora.serverconstants.LIBGEN_COVER_IMAGE_URL
import com.funkymuse.aurora.serverconstants.torrentDownloadURL
import com.funkymuse.composed.core.context
import com.funkymuse.composed.core.stateWhenStarted
import com.funkymuse.style.color.CardBackground
import com.funkymuse.style.color.PrimaryVariant
import com.funkymuse.style.shape.Shapes
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.insets.statusBarsPadding
import java.util.*

/**
 * Created by FunkyMuse, date 2/27/21
 */



@Composable
fun ShowDetailedBook(
        mirrors: Mirrors?,
        bookDetailsViewModel: BookDetailsViewModel = hiltViewModel(),
        internetDetectorViewModel: InternetDetectorViewModel = hiltViewModel(),
) {

    val onBackClicked = {
        bookDetailsViewModel.navigateUp()
    }
    val scope = rememberCoroutineScope()
    val book by stateWhenStarted(flow = bookDetailsViewModel.book, initial = RetrofitResult.Loading)

    val favoritesBook by stateWhenStarted(bookDetailsViewModel.favoriteBook, null)
    var detailedBook by remember { mutableStateOf<DetailedBookModel?>(null) }


    val retry = {
        bookDetailsViewModel.retry()
    }
    ScaffoldWithBackAndFavorites(
        book is RetrofitResult.Success,
        favoritesBook,
        onBackClicked = {
            onBackClicked()
        },
        onFavoritesClicked = {
            favoritesClick(favoritesBook, detailedBook, bookDetailsViewModel, mirrors)
        }
    ) {
        book.handle(
            loading = {
                LoadingBubbles()
            },
            emptyData = {
                ErrorWithRetry(R.string.no_book_loaded) {
                    retry()
                }
            },
            callError = { throwable ->
                if (throwable is NoConnectionException) {
                    retryOnConnectedToInternet(
                        internetDetectorViewModel,
                        scope
                    ) {
                        retry()
                    }
                    ErrorMessage(R.string.no_book_loaded_no_connect)
                } else {
                    ErrorWithRetry(R.string.no_book_loaded) {
                        retry()
                    }
                }
            },
            apiError = { _, _ ->
                ErrorWithRetry(R.string.no_book_loaded) {
                    retry()
                }
            },
            success = {
                detailedBook = firstOrNull()
                if (detailedBook == null) {
                    onBackClicked()
                    return@handle
                }
                detailedBook?.apply {
                    DetailedBook(this, mirrors?.list)
                }

            }
        )
    }

}

private fun favoritesClick(
        favoritesBook: FavoriteBook?,
        detailedBook: DetailedBookModel?,
        bookDetailsViewModel: BookDetailsViewModel,
        mirrors: Mirrors?
) {
    if (favoritesBook == null) {
        detailedBook?.let { bookModel ->
            bookDetailsViewModel.addToFavorites(
                FavoriteBook(
                    bookModel.id.toString().toInt(),
                    bookModel.title,
                    bookModel.year,
                    bookModel.pages,
                    bookModel.extension,
                    bookModel.author,
                    mirrors?.list
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
    DetailedBook(book = DetailedBookModel.testBook, listOf("test", "test"))
}


@Composable
fun DetailedBook(
        book: DetailedBookModel,
        dlMirrors: List<String>? = null
) {
    val scrollState = rememberScrollState()
    val imageUrl = LIBGEN_COVER_IMAGE_URL + book.coverurl
    val localContext = context

    Column(
        modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val alignment = Modifier.align(Alignment.Start)
        val imageModifier = Modifier
                .padding(top = 16.dp)

        val painter = rememberCoilPainter(imageUrl)
        when (painter.loadState) {
            is ImageLoadState.Loading -> {
                CardShimmer(imageHeight = 200.dp, imageWidth = 180.dp)
            }
            is ImageLoadState.Success, is ImageLoadState.Error -> {
                Card(shape = Shapes.large, modifier = imageModifier) {
                    Image(
                            painter = painter,
                            contentDescription = stringResource(id = R.string.book_details)
                    )
                }
            }
        }


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

        if (dlMirrors.isNotNullOrEmpty) {
            var menuExpanded by remember { mutableStateOf(false) }

            DetailedButton(stringResource(id = R.string.download_mirrors)) {
                menuExpanded = true
            }
            DropdownMenu(expanded = menuExpanded,
                modifier = Modifier.fillMaxWidth(),
                offset = DpOffset(32.dp, 16.dp),
                onDismissRequest = { menuExpanded = false }) {
                dlMirrors?.forEachIndexed { index, it ->
                    DropdownMenuItem(onClick = {
                        localContext.openWebPage(it)
                        menuExpanded = false
                    }) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = stringResource(id = R.string.download)
                        )

                        Text(
                            text = stringResource(id = R.string.mirror_placeholder, index + 1),
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
            Spacer(modifier = Modifier.padding(top = 16.dp, bottom = 46.dp))
        } else {
            Spacer(modifier = Modifier.padding(16.dp))
        }

    }
}

@Composable
fun TopAppBarBookDetails(
    onBackClicked: () -> Unit, isInFavorites: Boolean, showFavoritesButton: Boolean,
    onFavoritesClicked: () -> Unit
) {
    TopAppBar(backgroundColor = PrimaryVariant, modifier = Modifier.statusBarsPadding()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (backButton, favorites) = createRefs()
            BackButton(
                modifier = Modifier
                        .constrainAs(backButton) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(8.dp), onClick = onBackClicked
            )

            if (showFavoritesButton) {
                AddToFavorites(
                        Modifier
                                .constrainAs(favorites) {
                                    end.linkTo(parent.end)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                }
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
                .zIndex(2f)
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
        shape = Shapes.large,
        backgroundColor = CardBackground
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