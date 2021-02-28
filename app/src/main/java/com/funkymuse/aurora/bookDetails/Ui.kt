package com.funkymuse.aurora.bookDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.crazylegend.kotlinextensions.log.debug
import com.crazylegend.kotlinextensions.string.clearHtmlTags
import com.crazylegend.kotlinextensions.string.isNotNullOrEmpty
import com.crazylegend.retrofit.retrofitResult.handle
import com.funkymuse.aurora.R
import com.funkymuse.aurora.backButton.BackButton
import com.funkymuse.aurora.consts.LIBGEN_COVER_IMAGE_URL
import com.funkymuse.aurora.dto.DetailedBookModel
import com.funkymuse.aurora.extensions.CardShimmer
import com.funkymuse.aurora.extensions.GlideImageState
import com.funkymuse.aurora.extensions.assistedViewModel
import com.funkymuse.aurora.extensions.loadPicture
import com.funkymuse.aurora.ui.theme.Shapes
import java.util.*

/**
 * Created by FunkyMuse, date 2/27/21
 */

const val BOOK_DETAILS_ROUTE = "book_details"
const val BOOK_PARAM = "book"
const val BOOK_DETAILS_BOTTOM_NAV_ROUTE = "$BOOK_DETAILS_ROUTE/{$BOOK_PARAM}"

@SuppressLint("RestrictedApi")
@Composable
fun ShowDetailedBook(
    id: Int?,
    navController: NavHostController,
    bookDetailsViewModel: BookDetailsViewModel.BookDetailsVMF,
) {
    if (id == null) {
        return
    }
    val viewModel = viewModel<BookDetailsViewModel>(factory = assistedViewModel(
        owner = LocalSavedStateRegistryOwner.current
    ) {
        bookDetailsViewModel.create(id)
    })
    val book = viewModel.book.collectAsState().value
    book.handle(
        loading = {

        },
        emptyData = {

        },
        callError = { throwable ->

        },
        apiError = { errorBody, responseCode ->

        },
        success = {
            val detailedBook = firstOrNull()
            if (detailedBook == null)
                navController.navigateUp()
            DetailedBook(detailedBook) {
                navController.navigateUp()
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailedBook(book: DetailedBookModel? = null, onBackClicked: () -> Unit = {}) {
    val scrollState = rememberScrollState()
    val imageUrl = LIBGEN_COVER_IMAGE_URL + book?.coverurl
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val alignment = Modifier.align(Alignment.Start)
        val imageModifier = Modifier
            .padding(top = 16.dp)

        BackButton(modifier = alignment
            .padding(start = 16.dp, top= 8.dp), onClick = onBackClicked)

        when(val res = loadPicture(url = imageUrl).collectAsState().value){
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
            text = book?.author ?: stringResource(id = R.string.not_available),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp, textAlign = TextAlign.Center, color = Color.Gray,
            )
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = book?.title ?: stringResource(id = R.string.not_available),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp, textAlign = TextAlign.Center
            )
        )

        book?.descr?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.description_detail,
                    clearHtmlTags()
                )
        }

        book?.year?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.year,
                    this
                )
        }

        book?.language?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.language_detail,
                    this
                )
        }


        book?.pages?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.number_of_pages_detail,
                    this
                )
        }


        book?.extension?.toUpperCase(Locale.ROOT)?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.file_type_detail,
                    this
                )
        }

        book?.timeadded?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.time_added_detail,
                    this
                )
        }

        book?.timelastmodified?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.time_last_modified_detail,
                    this
                )
        }

        book?.edition?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.edition_detail,
                    this
                )
        }

        book?.publisher?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.publisher_detail,
                    this
                )
        }

        book?.series?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.series_detail,
                    this
                )
        }

        book?.volumeinfo?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.volume_info_detail,
                    this
                )
        }

        book?.periodical?.apply {
            if (isNotNullOrEmpty())
                TitleCardWithContent(
                    alignment,
                    R.string.periodical_detail,
                    this
                )
        }
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
        backgroundColor = Color(0xFF7F7F7F7)
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
