package com.funkymuse.aurora.bookdownloader

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */

@HiltWorker
class BookDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookFileDownloader: BookFileDownloader
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        val downloadUrl =
            inputData.getString(BookDownloadScheduler.URL_KEY) ?: return Result.failure()
        val bookId = inputData.getString(BookDownloadScheduler.BOOK_ID) ?: return Result.failure()
        val extension =
            inputData.getString(BookDownloadScheduler.EXTENSION) ?: return Result.failure()
        val bookName =
            inputData.getString(BookDownloadScheduler.BOOK_NAME) ?: return Result.failure()
        setForeground(bookFileDownloader.buildForegroundInfo(bookName))
        return bookFileDownloader.downloadFile(downloadUrl, bookId, extension, bookName)
    }


}