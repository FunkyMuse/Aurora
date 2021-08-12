package com.funkymuse.aurora.downloadsdata

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */
sealed class DownloadsModel {

    object Loading : DownloadsModel()
    data class Success(val filesModel: List<FileModel>) : DownloadsModel()
    object Empty : DownloadsModel()
}
