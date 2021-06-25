package com.funkymuse.aurora.book

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import com.funkymuse.aurora.R
import com.funkymuse.aurora.dto.Book
import com.funkymuse.aurora.dto.GeneralBook
import com.funkymuse.style.color.CardBackground
import com.funkymuse.style.shape.Shapes
import org.jsoup.nodes.Element
import org.jsoup.parser.Tag

/**
 * Created by FunkyMuse, date 2/25/21
 */


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showSystemUi = true, showBackground = true, device = Devices.PIXEL_4)
fun Book(
    book: GeneralBook = Book(Element(Tag.valueOf("div"), "test")),
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
        ConstraintLayout(
            modifier = Modifier
                .combinedClickable(onLongClick = onLongClick, onClick = onClick)
        ) {
            val image = addStaticImage()
            val title = addTitle(image, book.title)
            val author = addAuthor(title, image, book.author)
            AddYearNumberOfPagesAndFileFormat(author, book.year, book.pages, book.extension)
        }
    }
}

@Composable
@Preview(
    showSystemUi = true,
    showBackground = true,
    device = Devices.PIXEL_4,
    name = "loading book"
)
fun LoadingBook(book: Book = Book(Element(Tag.valueOf("div"), "test"))) {
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 300
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        shape = Shapes.large,
        modifier = Modifier
                .padding(16.dp, 8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .alpha(alpha),
        backgroundColor = Color.LightGray
    ) {
        ConstraintLayout {
            val image = addStaticImage()
            val title = addTitle(image, book.title)
            val author = addAuthor(title, image, book.author)
            AddYearNumberOfPagesAndFileFormat(author, book.year, book.pages, book.extension)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ConstraintLayoutScope.AddYearNumberOfPagesAndFileFormat(
    author: ConstrainedLayoutReference,
    yearText: String?,
    pagesText: String?,
    fileFormatText: String?
) {
    val (yearTitle, year, pagesTitle, pages, fileFormatTitle, fileFormat) = createRefs()

    val bottomBarrier = createGuidelineFromBottom(16.dp)

    Text(
        text = stringResource(id = R.string.year),
        modifier = Modifier
            .constrainAs(yearTitle) {
                start.linkTo(parent.start)
                top.linkTo(author.bottom, 4.dp)
                width = Dimension.percent(0.3333f)
            },
        textAlign = TextAlign.Center
    )

    Text(
        text = yearText ?: stringResource(id = R.string.not_available),
        modifier = Modifier
            .constrainAs(year) {
                start.linkTo(yearTitle.start)
                top.linkTo(yearTitle.bottom, 4.dp)
                bottom.linkTo(bottomBarrier)
                centerHorizontallyTo(yearTitle)
            }
    )

    Text(
        text = stringResource(id = R.string.of_pages),
        modifier = Modifier
            .constrainAs(pagesTitle) {
                start.linkTo(yearTitle.end)
                top.linkTo(author.bottom, 4.dp)
                width = Dimension.percent(0.3333f)
            },
        textAlign = TextAlign.Center
    )
    Text(
        text = pagesText?.toIntOrNull()?.toString() ?: pagesText?.take(5)
        ?: stringResource(id = R.string.not_available),
        modifier = Modifier
            .constrainAs(pages) {
                start.linkTo(pagesTitle.start)
                top.linkTo(pagesTitle.bottom, 4.dp)
                centerHorizontallyTo(pagesTitle)
            }
    )

    Text(
        text = stringResource(id = R.string.file_format),
        modifier = Modifier
            .constrainAs(fileFormatTitle) {
                start.linkTo(pagesTitle.end)
                top.linkTo(author.bottom, 4.dp)
                width = Dimension.percent(0.3333f)
            },
        textAlign = TextAlign.Center,
    )
    Text(
            text = fileFormatText?.uppercase() ?: stringResource(id = R.string.not_available),
            modifier = Modifier
                    .constrainAs(fileFormat) {
                        start.linkTo(fileFormatTitle.start)
                        top.linkTo(fileFormatTitle.bottom, 4.dp)
                        centerHorizontallyTo(fileFormatTitle)
                    }
    )


}

@Composable
private fun ConstraintLayoutScope.addAuthor(
    title: ConstrainedLayoutReference,
    image: ConstrainedLayoutReference,
    authorText: String?
): ConstrainedLayoutReference {
    val author = createRef()
    Text(
        text = authorText ?: stringResource(id = R.string.not_available),
        modifier = Modifier
            .constrainAs(author) {
                start.linkTo(image.end, 16.dp)
                top.linkTo(title.bottom, 4.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
            },
        fontStyle = FontStyle.Italic
    )
    return author
}

@Composable
private fun ConstraintLayoutScope.addTitle(
    image: ConstrainedLayoutReference,
    titleText: String?
): ConstrainedLayoutReference {
    val title = createRef()
    Text(
        text = titleText ?: stringResource(id = R.string.not_available),
        modifier = Modifier
            .constrainAs(title) {
                start.linkTo(image.end, 16.dp)
                top.linkTo(parent.top, 14.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
            },
        overflow = TextOverflow.Ellipsis,
        maxLines = 3,
        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    )
    return title
}

@Composable
private fun ConstraintLayoutScope.addStaticImage(): ConstrainedLayoutReference {
    val image = createRef()
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = stringResource(
            id = R.string.book_details
        ),
        modifier = Modifier
                .size(35.dp, 30.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
    )
    return image
}