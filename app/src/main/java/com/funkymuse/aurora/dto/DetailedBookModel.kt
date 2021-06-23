package com.funkymuse.aurora.dto


import android.os.Parcelable
import com.crazylegend.common.randomUUIDstring
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Parcelize
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
) : Parcelable{
        companion object{
                val testBook = DetailedBookModel(
                        author = randomUUIDstring,
                        coverurl = randomUUIDstring,
                        descr = randomUUIDstring,
                        edition = randomUUIDstring,
                        extension = randomUUIDstring,
                        id = randomUUIDstring,
                        language = randomUUIDstring,
                        md5 = randomUUIDstring,
                        pages = randomUUIDstring,
                        periodical = randomUUIDstring,
                        publisher = randomUUIDstring,
                        series = randomUUIDstring,
                        timeadded = randomUUIDstring,
                        timelastmodified = randomUUIDstring,
                        title = randomUUIDstring,
                        torrent = randomUUIDstring,
                        volumeinfo = randomUUIDstring,
                        year = randomUUIDstring
                )
        }
}