package com.funkymuse.aurora.commonextensions

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import java.io.File

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */

private const val DOWNLOADS_PATH = "downloads"


fun Context.downloads(): File {
    val localPath = filesDir
    val directory = File(localPath.path + File.separator + DOWNLOADS_PATH)
    directory.mkdirs()
    return directory
}


private val fileSizeUnits = arrayOf("bytes", "Kb", "Mb", "GB", "TB", "PB", "EB", "ZB", "YB")

fun Long.toFileSizeString(): String {
    var bytesToCalculate = this
    val sizeToReturn: String
    var index = 0
    while (index < fileSizeUnits.size) {
        if (bytesToCalculate < 1024) {
            break
        }
        bytesToCalculate /= 1024
        index++
    }
    sizeToReturn = bytesToCalculate.toString() + " " + fileSizeUnits[index]
    return sizeToReturn
}


fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
}

val String.decodeBase64: String get() = Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)

@SuppressLint("MissingPermission")
fun Context.hasVPN(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    return connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
}

val AndroidViewModel.context: Context get() = getApplication()
