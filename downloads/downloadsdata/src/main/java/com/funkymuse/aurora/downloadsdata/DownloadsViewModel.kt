package com.funkymuse.aurora.downloadsdata

import android.app.Application
import android.os.FileObserver
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.funkymuse.aurora.common.downloads
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val downloads: File = application.downloads()
    val files = downloads.fileEvents()

    private fun loadDownloads() : DownloadsModel {
        Log.d("LOADING", " AGAIN ?")
        val filesList = downloads.listFiles()?.toList()?.map {
            FileModel(
                it.nameWithoutExtension.substringBefore("-").trim(),
                it.length(),
                it.extension,
                it.nameWithoutExtension.substringAfter("(").removeSuffix(")"),
                it
            )
        } ?: emptyList()
        val removedDups = filesList.distinctBy { it.bookId }
        return if (removedDups.isEmpty()) DownloadsModel.Empty else DownloadsModel.Success(removedDups)
    }

    fun retry() {
        loadDownloads()
    }

    private fun File.fileEvents() = callbackFlow {
        val observer = object : FileObserver(this@fileEvents, MODIFY){
            override fun onEvent(event: Int, path: String?) {
                if (event == ACCESS){
                    Log.d("FUCKING EVENT", event.toString(16))
                    trySend(DownloadsModel.Loading)
                    trySend(loadDownloads())
                }
            }
        }
        trySend(loadDownloads())
        observer.startWatching()
        awaitClose { observer.stopWatching() }
    }.buffer(Channel.CONFLATED)
}