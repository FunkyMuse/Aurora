package com.funkymuse.aurora.bookdownloader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.funkymuse.aurora.appscope.ApplicationScope
import com.funkymuse.aurora.downloadsdestination.makeBookIdUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    @ApplicationScope private val appScope: CoroutineScope
) {

    private companion object {
        private const val REQUEST_CODE = 1337
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    fun foregroundInfoNotification(bookName: String): Notification =
        createNotification(bookName)

    fun publishNotification(progress: Int, bookName: String, bookId: String) {
        if (progress == 100) {
            appScope.launch {
                notifyDeepLinkedNotification(bookName, bookId)
            }
        } else {
            notificationManager.notify(
                BookDownloadScheduler.NOTIFICATION_ID,
                createNotification(bookName, progress)
            )
        }
    }

    private suspend fun notifyDeepLinkedNotification(bookName: String, bookId: String) {
        delay(350)
        notificationManager.notify(
            BookDownloadScheduler.NOTIFICATION_GO_TO_DOWNLOADS,
            goToDownloads(bookName, bookId)
        )
    }

    private fun goToDownloads(bookName: String, bookId: String): Notification {
        val title = context.getString(R.string.aurora_download_service)
        val downloading = context.getString(R.string.downloading_success_placeholder, bookName)

        val taskDetailIntent = Intent(
            Intent.ACTION_VIEW,
            makeBookIdUri(bookId)
        )
        Log.d("URI", makeBookIdUri(bookId).toString())

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(taskDetailIntent)
            getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder =
            NotificationCompat.Builder(context, BookDownloadScheduler.NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setTicker(title)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(downloading))
                .setContentText(downloading)
                .setSmallIcon(R.drawable.ic_logo)
                .setOnlyAlertOnce(true)

        return builder.build()
    }

    private fun createNotification(bookName: String, progress: Int = 0): Notification {
        val title = context.getString(R.string.aurora_download_service)
        val downloading = context.getString(R.string.downloading_placeholder, bookName)

        val builder =
            NotificationCompat.Builder(context, BookDownloadScheduler.NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setTicker(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(downloading))
                .setContentText(downloading)
                .setSmallIcon(R.drawable.ic_logo)
                .setOngoing(true)
                .setOnlyAlertOnce(true)

        if (progress == 0) {
            builder.setProgress(100, 0, true)
        } else {
            builder.setProgress(100, progress, false)
        }
        return builder.build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                BookDownloadScheduler.NOTIFICATION_CHANNEL,
                context.getString(R.string.download_books_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description =
                context.getString(R.string.download_books_channel_description)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}