package com.funkymuse.aurora.bookui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.funkymuse.aurora.generalbook.GeneralBook
import com.funkymuse.aurora.serverconstants.LIBGEN_BASE_URL
import com.funkymuse.style.shape.Shapes

/**
 * Created by FunkyMuse, date 2/25/21
 */


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Book(
    book: GeneralBook,
    onCopiedToClipBoard: (String) -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        shape = Shapes.large,
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(onLongClick = onLongClick, onClick = onClick)
        ) {
            Row(modifier = Modifier.width(IntrinsicSize.Max)) {
                Box(
                    modifier = Modifier
                        .weight(0.3f, false)
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
                        AddAuthor(authorText = book.author, onCopiedToClipBoard)
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
    val pagesNumber = if (pages == "0") "" else pages

    val pagesText =
        if (pagesNumber.isNullOrBlank()) "" else "($pages ${stringResource(id = R.string.pages)})"

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
        style = TextStyle(fontWeight = FontWeight.Light, fontSize = 17.sp, fontStyle = FontStyle.Italic),
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun AddYear(year: String?) {
    year ?: return
    Text(
        text = year,
        style = TextStyle(fontWeight = FontWeight.Light, fontSize = 17.sp, fontStyle = FontStyle.Italic)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AddAuthor(
    authorText: String?,
    onCopiedToClipBoard: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val authorCopyRes = stringResource(id = R.string.author_copied_to_clipboard)
    Text(
        text = authorText ?: stringResource(id = R.string.not_available),
        modifier = Modifier
            .padding(end = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    authorText?.let { clipboardManager.setText(AnnotatedString(it)) }
                    onCopiedToClipBoard(authorCopyRes)
                })
            },
        fontStyle = FontStyle.Italic
    )
}

@Composable
private fun AddTitle(
    titleText: String?
) {
    Text(
        modifier = Modifier.padding(end = 8.dp, top = 8.dp),
        text = titleText ?: stringResource(id = R.string.not_available),
        overflow = TextOverflow.Ellipsis,
        maxLines = 3,
        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    )
}

@Composable
private fun AddStaticImage(remoteImage: String?) {
    val imageUrl = LIBGEN_BASE_URL + remoteImage

    val size = Size(85.dp.value, 130.dp.value)

    val imageModifier = Modifier
        .size(size.width.dp, size.height.dp)


    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = imageModifier
            .clip(RectangleShape),
    )
}