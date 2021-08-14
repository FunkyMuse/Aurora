package com.funkymuse.aurora.downloadsdata

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.funkymuse.aurora.common.toFileSizeString
import java.io.File


/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */
data class FileModel(
    val fileName: String,
    val fileSize: Long,
    val extension: String,
    val bookId: String,
    val file: File
) {
    val size get() = fileSize.toFileSizeString()
    private val fileUri: Uri get() = Uri.fromFile(file)
    val fileNameAndExtension get() = "$fileName.$extension"

    fun getMimeType(context: Context): String? {
        return if (ContentResolver.SCHEME_CONTENT == fileUri.scheme) {
            context.contentResolver.getType(fileUri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
    }
}
