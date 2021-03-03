package com.funkymuse.aurora.dto


import android.os.Parcelable
import com.crazylegend.kotlinextensions.randomUUIDstring
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Parcelize
@JsonClass(generateAdapter = true)
data class DetailedBookModel(
        @Json(name = "aich")
        val aich: String? = null,
        @Json(name = "asin")
        val asin: String? = null,
        @Json(name = "author")
        val author: String? = null,
        @Json(name = "bookmarked")
        val bookmarked: String? = null,
        @Json(name = "btih")
        val btih: String? = null,
        @Json(name = "city")
        val city: String? = null,
        @Json(name = "cleaned")
        val cleaned: String? = null,
        @Json(name = "color")
        val color: String? = null,
        @Json(name = "commentary")
        val commentary: String? = null,
        @Json(name = "coverurl")
        val coverurl: String? = null,
        @Json(name = "crc32")
        val crc32: String? = null,
        @Json(name = "ddc")
        val ddc: String? = null,
        @Json(name = "descr")
        val descr: String? = null,
        @Json(name = "doi")
        val doi: String? = null,
        @Json(name = "dpi")
        val dpi: String? = null,
        @Json(name = "edition")
        val edition: String? = null,
        @Json(name = "edonkey")
        val edonkey: String? = null,
        @Json(name = "extension")
        val extension: String? = null,
        @Json(name = "filesize")
        val filesize: String? = null,
        @Json(name = "generic")
        val generic: String? = null,
        @Json(name = "googlebookid")
        val googlebookid: String? = null,
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "identifier")
        val identifier: String? = null,
        @Json(name = "identifierwodash")
        val identifierwodash: String? = null,
        @Json(name = "issn")
        val issn: String? = null,
        @Json(name = "issue")
        val issue: String? = null,
        @Json(name = "language")
        val language: String? = null,
        @Json(name = "lbc")
        val lbc: String? = null,
        @Json(name = "lcc")
        val lcc: String? = null,
        @Json(name = "library")
        val library: String? = null,
        @Json(name = "local")
        val local: String? = null,
        @Json(name = "locator")
        val locator: String? = null,
        @Json(name = "md5")
        val md5: String? = null,
        @Json(name = "openlibraryid")
        val openlibraryid: String? = null,
        @Json(name = "orientation")
        val orientation: String? = null,
        @Json(name = "pages")
        val pages: String? = null,
        @Json(name = "pagesinfile")
        val pagesinfile: String? = null,
        @Json(name = "paginated")
        val paginated: String? = null,
        @Json(name = "periodical")
        val periodical: String? = null,
        @Json(name = "publisher")
        val publisher: String? = null,
        @Json(name = "scanned")
        val scanned: String? = null,
        @Json(name = "searchable")
        val searchable: String? = null,
        @Json(name = "series")
        val series: String? = null,
        @Json(name = "sha1")
        val sha1: String? = null,
        @Json(name = "sha256")
        val sha256: String? = null,
        @Json(name = "tags")
        val tags: String? = null,
        @Json(name = "timeadded")
        val timeadded: String? = null,
        @Json(name = "timelastmodified")
        val timelastmodified: String? = null,
        @Json(name = "title")
        val title: String? = null,
        @Json(name = "toc")
        val toc: String? = null,
        @Json(name = "topic")
        val topic: String? = null,
        @Json(name = "torrent")
        val torrent: String? = null,
        @Json(name = "tth")
        val tth: String? = null,
        @Json(name = "udc")
        val udc: String? = null,
        @Json(name = "visible")
        val visible: String? = null,
        @Json(name = "volumeinfo")
        val volumeinfo: String? = null,
        @Json(name = "year")
        val year: String? = null
) : Parcelable{
        companion object{
                val testBook = DetailedBookModel(
                        aich = randomUUIDstring,
                        asin = randomUUIDstring,
                        author = randomUUIDstring,
                        bookmarked = randomUUIDstring,
                        btih = randomUUIDstring,
                        city = randomUUIDstring,
                        cleaned = randomUUIDstring,
                        color = randomUUIDstring,
                        commentary = randomUUIDstring,
                        coverurl = randomUUIDstring,
                        crc32 = randomUUIDstring,
                        ddc = randomUUIDstring,
                        descr = randomUUIDstring,
                        doi = randomUUIDstring,
                        dpi = randomUUIDstring,
                        edition = randomUUIDstring,
                        edonkey = randomUUIDstring,
                        extension = randomUUIDstring,
                        filesize = randomUUIDstring,
                        generic = randomUUIDstring,
                        googlebookid = randomUUIDstring,
                        id = randomUUIDstring,
                        identifier = randomUUIDstring,
                        identifierwodash = randomUUIDstring,
                        issn = randomUUIDstring,
                        issue = randomUUIDstring,
                        language = randomUUIDstring,
                        lbc = randomUUIDstring,
                        lcc = randomUUIDstring,
                        library = randomUUIDstring,
                        local = randomUUIDstring,
                        locator = randomUUIDstring,
                        md5 = randomUUIDstring,
                        openlibraryid = randomUUIDstring,
                        orientation = randomUUIDstring,
                        pages = randomUUIDstring,
                        pagesinfile = randomUUIDstring,
                        paginated = randomUUIDstring,
                        periodical = randomUUIDstring,
                        publisher = randomUUIDstring,
                        scanned = randomUUIDstring,
                        searchable = randomUUIDstring,
                        series = randomUUIDstring,
                        sha1 = randomUUIDstring,
                        sha256 = randomUUIDstring,
                        tags = randomUUIDstring,
                        timeadded = randomUUIDstring,
                        timelastmodified = randomUUIDstring,
                        title = randomUUIDstring,
                        toc = randomUUIDstring,
                        topic = randomUUIDstring,
                        torrent = randomUUIDstring,
                        tth = randomUUIDstring,
                        udc = randomUUIDstring,
                        visible = randomUUIDstring,
                        volumeinfo = randomUUIDstring,
                        year = randomUUIDstring
                )
        }
}