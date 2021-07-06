package com.funkymuse.bookdetails.bookdetailsmodel


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@JsonClass(generateAdapter = true)
data class DetailedBookModel(
        @Json(name = "author")
        val author: String? = null,
        @Json(name = "coverurl")
        val coverurl: String? = null,
        @Json(name = "descr")
        val descr: String? = null,
        @Json(name = "edition")
        val edition: String? = null,
        @Json(name = "extension")
        val extension: String? = null,
        @Json(name = "id")
        val id: String? = null,
        @Json(name = "language")
        val language: String? = null,
        @Json(name = "md5")
        val md5: String? = null,
        @Json(name = "pages")
        val pages: String? = null,
        @Json(name = "periodical")
        val periodical: String? = null,
        @Json(name = "publisher")
        val publisher: String? = null,
        @Json(name = "series")
        val series: String? = null,
        @Json(name = "timeadded")
        val timeadded: String? = null,
        @Json(name = "timelastmodified")
        val timelastmodified: String? = null,
        @Json(name = "title")
        val title: String? = null,
        @Json(name = "torrent")
        val torrent: String? = null,
        @Json(name = "volumeinfo")
        val volumeinfo: String? = null,
        @Json(name = "year")
        val year: String? = null
) {
    companion object {
        val testBook = DetailedBookModel(
                author = UUID.randomUUID().toString(),
                coverurl = UUID.randomUUID().toString(),
                descr = UUID.randomUUID().toString(),
                edition = UUID.randomUUID().toString(),
                extension = UUID.randomUUID().toString(),
                id = UUID.randomUUID().toString(),
                language = UUID.randomUUID().toString(),
                md5 = UUID.randomUUID().toString(),
                pages = UUID.randomUUID().toString(),
                periodical = UUID.randomUUID().toString(),
                publisher = UUID.randomUUID().toString(),
                series = UUID.randomUUID().toString(),
                timeadded = UUID.randomUUID().toString(),
                timelastmodified = UUID.randomUUID().toString(),
                title = UUID.randomUUID().toString(),
                torrent = UUID.randomUUID().toString(),
                volumeinfo = UUID.randomUUID().toString(),
                year = UUID.randomUUID().toString()
        )
    }
}