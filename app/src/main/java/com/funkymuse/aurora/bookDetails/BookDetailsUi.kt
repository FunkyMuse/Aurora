package com.funkymuse.aurora.bookDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import androidx.navigation.NavHostController
import com.crazylegend.kotlinextensions.collections.isNotNullOrEmpty
import com.crazylegend.kotlinextensions.intent.openWebPage
import com.crazylegend.kotlinextensions.string.clearHtmlTags
import com.crazylegend.kotlinextensions.string.isNotNullOrEmpty
import com.crazylegend.retrofit.retrofitResult.handle
import com.crazylegend.retrofit.retryOnConnectedToInternet
import com.crazylegend.retrofit.throwables.NoConnectionException
import com.funkymuse.aurora.R
import com.funkymuse.aurora.backButton.BackButton
import com.funkymuse.aurora.components.ScaffoldWithBack
import com.funkymuse.aurora.consts.LIBGEN_COVER_IMAGE_URL
import com.funkymuse.aurora.consts.torrentDownloadURL
import com.funkymuse.aurora.dto.DetailedBookModel
import com.funkymuse.aurora.dto.FavoriteBook
import com.funkymuse.aurora.dto.Mirrors
import com.funkymuse.aurora.extensions.*
import com.funkymuse.aurora.ui.theme.CardBackground
import com.funkymuse.aurora.ui.theme.PrimaryVariant
import com.funkymuse.aurora.ui.theme.Shapes
import java.util.*

/**
 * Created by FunkyMuse, date 2/27/21
 */

const val BOOK_DETAILS_ROUTE = "book_details"
const val BOOK_ID_PARAM = "book"
const val BOOK_MIRRORS_PARAM = "mirrors"
const val BOOK_DETAILS_BOTTOM_NAV_ROUTE = "$BOOK_DETAILS_ROUTE/{$BOOK_ID_PARAM}"

@SuppressLint("RestrictedApi")
@Composable
fun ShowDetailedBook(
    id: Int?,
    mirrors: Mirrors?,
    navController: NavHostController,
    bookDetailsViewModel: BookDetailsViewModel.BookDetailsVMF,
) {
    if (id == null) {
        return
    }
    val viewModel = assistedViewModel { bookDetailsViewModel.create(id) }
    val scope = rememberCoroutineScope()
    val book = viewModel.book.collectAsState().value

    val favoritesBook = viewModel.favoriteBook.collectAsState().value
    book.handle(
        loading = {
            Loading()
        },
        emptyData = {
            ScaffoldWithBack() {
                navController.navigateUp()
            }
        },
        callError = { throwable ->
            if (throwable is NoConnectionException){
                retryOnConnectedToInternet(viewModel.internetConnection,
                scope){
                    viewModel.retry()
                }
                ScaffoldWithBack() {
                    navController.navigateUp()
                }
            } else {
                ScaffoldWithBack(true,
                    onRetryClicked = {
                    viewModel.retry()
                }) {
                    navController.navigateUp()
                }
            }

        },
        apiError = { _, _ ->
            ScaffoldWithBack(true, onRetryClicked = {
                viewModel.retry()
            }) {
                navController.navigateUp()
            }
        },
        success = {
            val detailedBook = firstOrNull()
            if (detailedBook == null) {
                navController.navigateUp()
                return
            }

            DetailedBook(detailedBook, mirrors?.list, favoritesBook, onFavoritesClicked = {
                if (favoritesBook == null) {
                    viewModel.addToFavorites(
                        FavoriteBook(
                            detailedBook.id.toString().toInt(),
                            detailedBook.title,
                            detailedBook.year,
                            detailedBook.pages,
                            detailedBook.extension,
                            detailedBook.author,
                            mirrors?.list
                        )
                    )
                } else {
                    viewModel.removeFromFavorites(favoritesBook.id)
                }
            }) {
                navController.navigateUp()
            }
        }
    )
}



@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4_XL, name = "Book")
@Composable
fun BookPreview() {
    DetailedBook(book = DetailedBookModel.testBook, listOf("test", "test")) {

    }
}


@Composable
fun DetailedBook(
    book: DetailedBookModel,
    dlMirrors: List<String>? = null,
    favoritesBook: FavoriteBook? = null,
    onFavoritesClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val imageUrl = LIBGEN_COVER_IMAGE_URL + book.coverurl
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBarBookDetails(onBackClicked, favoritesBook != null) {
                onFavoritesClicked()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val alignment = Modifier.align(Alignment.Start)
            val imageModifier = Modifier
                .padding(top = 16.dp)

            when (val res = loadPicture(url = imageUrl).collectAsState().value) {
                is GlideImageState.Failure -> {
                    res.errorDrawable?.let {
                        Image(
                            bitmap = it,
                            contentDescription = stringResource(id = R.string.book_details)
                        )
                    }
                }
                GlideImageState.Loading -> {
                    CardShimmer(imageHeight = 200.dp, imageWidth = 180.dp)
                }
                is GlideImageState.Success -> {
                    Card(shape = Shapes.large, modifier = imageModifier) {
                        res.imageBitmap?.let {
                            Image(
                                bitmap = it,
                                contentDescription = stringResource(id = R.string.book_details)
                            )
                        }
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


            book.extension?.toUpperCase(Locale.ROOT)?.apply {
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
                            context.openWebPage(it)
                            menuExpanded = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download),
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
                    context.openWebPage(torrentDownloadURL(book.md5.toString()))
                }
                Spacer(modifier = Modifier.padding(16.dp))
            } else {
                Spacer(modifier = Modifier.padding(16.dp))
            }

        }
    }
}

@Composable
fun TopAppBarBookDetails(
    onBackClicked: () -> Unit, isInFavorites: Boolean,
    onFavoritesClicked: () -> Unit
) {
    TopAppBar(backgroundColor = PrimaryVariant) {
        BackButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp), onClick = onBackClicked
        )
        AddToFavorites(
            Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp), isInFavorites, onFavoritesClicked
        )
    }
}

@Composable
fun TopAppBarBackOnly(
    onBackClicked: () -> Unit
) {
    TopAppBar(backgroundColor = PrimaryVariant) {
        BackButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp), onClick = onBackClicked
        )
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