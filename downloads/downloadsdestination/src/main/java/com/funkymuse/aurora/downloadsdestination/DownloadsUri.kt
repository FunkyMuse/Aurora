package com.funkymuse.aurora.downloadsdestination

import android.net.Uri
import androidx.core.net.toUri

/**
 * Created by funkymuse on 8/14/21 to long live and prosper !
 */
private const val URI_ID = "com.funkymuse.aurora"
private val baseUri = "app://$URI_ID".toUri()

const val BOOK_ID_PARAM = "book_id"
private val URI_TO_PARAM = "$baseUri/$DOWNLOADS/$BOOK_ID_PARAM"

val DOWNLOADED_BOOK_NAME_URI_PATTERN = "$URI_TO_PARAM={$BOOK_ID_PARAM}"
fun makeBookIdUri(bookName: String): Uri = "$URI_TO_PARAM=$bookName".toUri()

