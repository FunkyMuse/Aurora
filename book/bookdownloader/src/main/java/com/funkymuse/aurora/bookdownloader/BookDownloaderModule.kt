package com.funkymuse.aurora.bookdownloader

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

/**
 * Created by funkymuse on 8/9/21 to long live and prosper !
 */

@Module
@InstallIn(SingletonComponent::class)
object BookDownloaderModule {

    private const val DOWNLOADS_PATH = "downloads"

    @Provides
    fun workManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)


    @Provides
    fun constraints(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresStorageNotLow(true)
        .build()

    @Provides
    fun notificationManager(@ApplicationContext context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    @Provides
    @BookPath
    fun localPath(@ApplicationContext context: Context): File {
        val localPath = context.filesDir
        val directory = File(localPath.path + File.separator + DOWNLOADS_PATH)
        directory.mkdirs()
        return directory
    }

}