package com.funkymuse.aurora.settingsui

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by funkymuse on 6/29/21 to long live and prosper !
 */

internal inline fun Context.openWebPage(url: String, onCantHandleAction: () -> Unit = {}) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    try {
        startActivity(intent)
    } catch (t: Throwable) {
        onCantHandleAction()
    }
}