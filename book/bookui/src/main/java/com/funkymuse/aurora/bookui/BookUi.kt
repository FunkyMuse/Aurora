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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.funkymuse.aurora.generalbook.GeneralBook
import com.funkymuse.aurora.loadingcomponent.BoxShimmer
import com.funkymuse.aurora.loadingcomponent.CardShimmer
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
        ConstraintLayout(
            modifier = Modifier
                .combinedClickable(onLongClick = onLongClick, onClick = onClick)
        ) {
            val image = image(book.image)
            val title = addTitle(image, book.title)
            addAuthor(title, image, book.author)
        }
    }
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
                top.linkTo(title.bottom, 8.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
            },
        style = TextStyle(fontStyle = FontStyle.Italic, fontSize = 16.sp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
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
                top.linkTo(parent.top, 10.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
            },
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    )
    return title
}

@Composable
private fun ConstraintLayoutScope.image(remoteImage: String?): ConstrainedLayoutReference {

    val imageUrl = LIBGEN_BASE_URL + remoteImage
    val painter = rememberImagePainter(data = imageUrl)

    val image = createRef()

    val size = Size(85.dp.value, 130.dp.value)

    val imageModifier = Modifier
        .size(size.width.dp, size.height.dp)
        .constrainAs(image) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
        }

    when (painter.state) {
        is ImagePainter.State.Loading -> {
            Box(modifier = imageModifier) {
                BoxShimmer(padding = 0.dp, imageHeight = size.height.dp, imageWidth = size.width.dp)
            }
        }
        is ImagePainter.State.Success, is ImagePainter.State.Error, ImagePainter.State.Empty -> {
            Box(modifier = imageModifier, contentAlignment = Alignment.CenterStart) {
                Image(
                    painter = painter,
                    contentDescription = stringResource(id = R.string.book_details)
                )
            }
        }
    }

    return image
}

@Preview
@Composable
fun BookPreview(){
    Book(book = com.funkymuse.aurora.bookmodel.Book("/covers/23000/897294668dce7c4cc065e7d7d96a4923-d.jpg",
    title = "Communicating Design: Developing Web Site Documentation for Design and Planning",
    author = "Dan M. Brown", "897294668DCE7C4CC065E7D7D96A4923"))
}