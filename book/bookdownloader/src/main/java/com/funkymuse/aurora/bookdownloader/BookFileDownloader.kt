package com.funkymuse.aurora.bookdownloader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.net.Uri
import android.os.Build
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import com.crazylegend.toaster.Toaster
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject


/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */
class BookFileDownloader @Inject constructor(
    @BookPath private val localPath: File,
    private val notificationHelper: NotificationHelper,
    private val toaster: Toaster,
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TIMEOUT = 10000
    }

    fun buildForegroundInfo(
        bookName: String
    ): ForegroundInfo {
        val notification = notificationHelper.foregroundInfoNotification(bookName)
        val notificationId = BookDownloadScheduler.NOTIFICATION_ID

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }


    fun downloadFile(
        downloadUrl: String,
        bookId: String,
        extension: String,
        bookName: String,
    ): ListenableWorker.Result = try {
        downloadFileWithProgress(
            downloadUrl,
            File(localPath.path, "$bookName - ($bookId).${extension.lowercase()}").path,
            progress = {
                notificationHelper.publishNotification(it, bookName)
            })
        ListenableWorker.Result.success()
    } catch (t: SocketTimeoutException) {
        toaster.longToast(R.string.server_time_out)
        pasteToClipboard(downloadUrl)
        ListenableWorker.Result.failure()
    }

    private fun pasteToClipboard(downloadUrl: String) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.apply {
            setPrimaryClip(ClipData.newPlainText("download url", downloadUrl))
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