package com.funkymuse.aurora.dto


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Parcelize
data class DetailedBookModel(
        val aich: String? = null,
        val asin: String? = null,
        val author: String? = null,
        val bookmarked: String? = null,
        val btih: String? = null,
        val city: String? = null,
        val cleaned: String? = null,
        val color: String? = null,
        val commentary: String? = null,
        val coverurl: String? = null,
        val crc32: String? = null,
        val ddc: String? = null,
        val descr: String? = null,
        val doi: String? = null,
        val dpi: String? = null,
        val edition: String? = null,
        val edonkey: String? = null,
        val extension: String? = null,
        val filesize: String? = null,
        val generic: String? = null,
        val googlebookid: String? = null,
        val id: String? = null,
        val identifier: String? = null,
        val identifierwodash: String? = null,
        val issn: String? = null,
        val issue: String? = null,
        val language: String? = null,
        val lbc: String? = null,
        val lcc: String? = null,
        val library: String? = null,
        val local: String? = null,
        val locator: String? = null,
        val md5: String? = null,
        val openlibraryid: String? = null,
        val orientation: String? = null,
        val pages: String? = null,
        val pagesinfile: String? = null,
        val paginated: String? = null,
        val periodical: String? = null,
        val publisher: String? = null,
        val scanned: String? = null,
        val searchable: String? = null,
        val series: String? = null,
        val sha1: String? = null,
        val sha256: String? = null,
        val tags: String? = null,
        val timeadded: String? = null,
        val timelastmodified: String? = null,
        val title: String? = null,
        val toc: String? = null,
        val topic: String? = null,
        val torrent: String? = null,
        val tth: String? = null,
        val udc: String? = null,
        val visible: String? = null,
        val volumeinfo: String? = null,
        val year: String? = null
) : Parcelable