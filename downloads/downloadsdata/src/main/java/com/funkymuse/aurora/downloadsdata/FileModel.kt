package com.funkymuse.aurora.downloadsdata

import com.funkymuse.aurora.common.toFileSizeString

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */
data class FileModel(
    val fileName: String,
    val fileSize: Long,
    val extension: String,
    val bookId: String
){
     val size get() = fileSize.toFileSizeString()

}
