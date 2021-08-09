package com.funkymuse.aurora.bookdownloader

import android.annotation.SuppressLint
import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */
class BookDownloadScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val constraints: Constraints
) {

    companion object {
        const val URL_KEY = "url"
        const val BOOK_ID = "bookId"
        const val EXTENSION = "extension"
        const val BOOK_NAME = "bookName"
        const val NOTIFICATION_ID = 69
        const val NOTIFICATION_CHANNEL = "420"
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun scheduleDownload(url: String, bookID: String, extension: String, bookName: String) {
        val downloadBookRequest =
            OneTimeWorkRequestBuilder<BookDownloadWorker>()
                .setInputData(buildData(url, bookID,extension, bookName))
                .addTag(url)
                //.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .keepResultsForAtLeast(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        workManager.cancelAllWorkByTag(url)
        workManager.enqueue(downloadBookRequest)
    }

    private fun buildData(url: String, bookID: String, extension: String, bookName:String): Data =
        Data.Builder()
            .putString(URL_KEY, url)
            .putString(BOOK_ID, bookID)
            .putString(EXTENSION, extension)
            .putString(BOOK_NAME, bookName)
            .build()

}