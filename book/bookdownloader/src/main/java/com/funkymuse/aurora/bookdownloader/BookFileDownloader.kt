package com.funkymuse.aurora.bookdownloader

import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.inject.Inject


/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */
class BookFileDownloader @Inject constructor(
    @BookPath private val localPath: File,
    private val notificationHelper: NotificationHelper
) {

    companion object {
        private const val TIMEOUT = 10000
    }

    fun buildForegroundInfo(
        id: UUID,
        bookName: String
    ): ForegroundInfo {
        val notification = notificationHelper.foregroundInfoNotification(id, bookName)
        val notificationId = BookDownloadScheduler.NOTIFICATION_ID

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }


    fun downloadFile(
        downloadUrl: String,
        id: UUID,
        bookId: String,
        extension: String,
        bookName: String
    ): ListenableWorker.Result {

        try {
            downloadFileWithProgress(
                downloadUrl,
                File(localPath.path, "$bookName - ($bookId).${extension.lowercase()}").path,
                connectionCallBack = {
                    Log.d("CONNECTION", "CODE $it")
                },
                onError = {
                    it.printStackTrace()
                },
                progress = {
                    notificationHelper.publishNotification(id, it, bookName)
                    Log.d("PROGRESS", "$it")
                }) {
                Log.d("CALLBACK", it.toString())
            }

            return ListenableWorker.Result.success()
        } catch (t: Throwable) {
            return ListenableWorker.Result.failure()
        }
    }

    private fun downloadFileWithProgress(
        urlPath: String,
        localPath: String,
        connectionCallBack: (responseCode: Int) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        progress: (Int) -> Unit = {},
        callback: (Uri?) -> Unit = {}
    ) {

        val uri = Uri.fromFile(File(localPath))
        val connection = URL(urlPath).openConnection() as HttpURLConnection
        connection.connectTimeout = TIMEOUT
        connection.readTimeout = TIMEOUT

        val input = connection.inputStream
        val output = FileOutputStream(File(uri.path))
        try {
            connection.connect()

            val responseCode = connection.responseCode
            connectionCallBack(responseCode)
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return
            }

            val fileLength = connection.contentLength
            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                if (fileLength > 0)
                    progress((total * 100 / fileLength).toInt())
                output.write(data, 0, count)
            }
        } catch (e: Throwable) {
            onError(e)
            return
        } finally {
            try {
                output.close()
                input?.close()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            connection.disconnect()
        }
        callback(uri)
    }
}