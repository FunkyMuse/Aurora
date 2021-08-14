package com.funkymuse.aurora.common

import android.content.Context
import java.io.File

/**
 * Created by funkymuse on 8/12/21 to long live and prosper !
 */

private const val DOWNLOADS_PATH = "downloads"


fun Context.downloads() : File {
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