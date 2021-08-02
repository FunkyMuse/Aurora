package com.funkymuse.aurora.bookui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.funkymuse.aurora.generalbook.GeneralBook
import com.funkymuse.aurora.loadingcomponent.BoxShimmer
import com.funkymuse.aurora.serverconstants.LIBGEN_BASE_URL
import com.funkymuse.style.color.CardBackground
import com.funkymuse.style.shape.Shapes

/**
 * Created by FunkyMuse, date 2/25/21
 */


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Book(
    book: GeneralBook,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        shape = Shapes.large,
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        backgroundColor = CardBackground
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(onLongClick = onLongClick, onClick = onClick)
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(16.dp)
                ) {
                    AddStaticImage(remoteImage = book.image)
                }
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(8.dp)
                ) {
                    Column {
                        AddTitle(titleText = book.title)
                        AddAuthor(authorText = book.author)
                        AddYear(year = book.year)
                        AddFormatPagesAndSize(book.extension, book.pages, book.size)
                    }
                }
            }
        }
    }
}

@Composable
fun AddFormatPagesAndSize(extension: String?, pages: String?, size: String?) {
    val pagesText =
        if (pages.isNullOrBlank()) "" else "($pages ${stringResource(id = R.string.pages)})"
    val extensionText = if (extension.isNullOrBlank()) "" else extension
    val sizeText = if (size.isNullOrBlank()) "" else size

    val text = when {
        pages.isNullOrBlank() -> "$extensionText, $sizeText"
        extension.isNullOrBlank() -> "$pagesText, $sizeText"
        size.isNullOrBlank() -> "$extensionText $pagesText"
        else -> "$extensionText $pagesText, $sizeText"
    }

    if (pages.isNullOrBlank() && extension.isNullOrBlank() && size.isNullOrBlank()) {
        return
    }

    Text(
        text = text,
        color = Color.DarkGray,
        style = TextStyle(fontWeight = FontWeight.Light, fontSize = 17.sp),
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun AddYear(year: String?) {
    year ?: return
    Text(
        text = year,
        color = Color.DarkGray,
        style = TextStyle(fontWeight = FontWeight.Light, fontSize = 17.sp)
    )
}

@Composable
private fun AddAuthor(
    authorText: String?
) {
    Text(
        text = authorText ?: stringResource(id = R.string.not_available),
        modifier = Modifier.padding(end = 8.dp),
        fontStyle = FontStyle.Italic
    )
}

@Composable
private fun AddTitle(
    titleText: String?
) {
    Text(
        modifier = Modifier.padding(end = 8.dp),
        text = titleText ?: stringResource(id = R.string.not_available),
        overflow = TextOverflow.Ellipsis,
        maxLines = 3,
        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    )
}

@Composable
private fun AddStaticImage(remoteImage: String?) {
    val imageUrl = LIBGEN_BASE_URL + remoteImage
    val painter = rememberImagePainter(data = imageUrl)

    val size = Size(85.dp.value, 130.dp.value)

    val imageModifier = Modifier
        .size(size.width.dp, size.height.dp)

    when (painter.state) {
        is ImagePainter.State.Loading -> {
            Box(modifier = imageModifier) {
                BoxShimmer(padding = 0.dp, imageHeight = size.height.dp, imageWidth = size.width.dp)
            }
        }
        is ImagePainter.State.Success, is ImagePainter.State.Error, ImagePainter.State.Empty -> {
            Box(modifier = imageModifier, contentAlignment = Alignment.Center) {
                Image(
                    painter = painter,
                    contentDescription = stringResource(id = R.string.book_details)
                )
            }
        }
    }
}