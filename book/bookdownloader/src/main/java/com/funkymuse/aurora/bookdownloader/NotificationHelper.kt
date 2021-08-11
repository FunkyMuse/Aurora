package com.funkymuse.aurora.bookdownloader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
) {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    fun foregroundInfoNotification(bookName: String): Notification =
        createNotification(bookName)

    fun publishNotification(progress: Int, bookName: String) {
        notificationManager.notify(
            BookDownloadScheduler.NOTIFICATION_ID,
            createNotification(bookName, progress)
        )
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