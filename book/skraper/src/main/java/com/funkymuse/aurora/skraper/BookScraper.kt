package com.funkymuse.aurora.skraper

import android.util.Log
import com.crazylegend.common.tryOrNull
import com.funkymuse.aurora.bookmodel.Book
import com.funkymuse.aurora.extensions.removeBrackets
import com.funkymuse.aurora.serverconstants.*
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 8/2/21 to long live and prosper !
 */
@Singleton
class BookScraper @Inject constructor() {

    private fun List<DocElement>.toBook(): Book? {
        val id = tryOrNull {
            this[2].eachLink.values.firstOrNull()?.substringAfter("md5=")
        } ?: return null
        val extension = tryOrNull { this[33].text }?.uppercase()
        val size = tryOrNull { this[31].text }?.substringBefore("(")?.trim()
        val pages = tryOrNull { this[21].text }?.removeBrackets()?.substringBefore(" ")
        val image = tryOrNull { this[0].eachImage.values.firstOrNull() }
        val title = tryOrNull { this[2].text }
        val author = tryOrNull { this[5].text }
        val year = tryOrNull { this[15].text }
        return Book(image, title, author, id, extension, pages, size, year)
    }

    fun generateSearchDataUrl(
        page: Int, searchQuery: String, sortQuery: String, sortType: String,
        searchInFieldsPosition: Int, maskWord: Boolean
    ): String = "$SEARCH_BASE_URL?$REQ_CONST=${
        searchQuery.replace(
            " ",
            "+"
        )
    }&$SORT_QUERY=$sortQuery&$VIEW_QUERY=$VIEW_QUERY_PARAM&$RES_CONST=$PAGE_SIZE&" +
            "&$COLUM_QUERY=${getFieldParamByPosition(searchInFieldsPosition)}&$SORT_TYPE=$sortType&" +
            "$SEARCH_WITH_MASK=${if (maskWord) SEARCH_WITH_MASK_YES else SEARCH_WITH_MASK_NO}&" +
            "$PAGE_CONST=$page"


    fun generateLatestBooksUrl(page: Int, sortQuery: String, sortType: String): String =
        "$SEARCH_BASE_URL?$SORT_QUERY=$sortQuery&$VIEW_QUERY=$VIEW_QUERY_PARAM&$RES_CONST=$PAGE_SIZE&" +
                "$LAST_MODE=$LAST_QUERY&$COLUM_QUERY=$FIELD_DEFAULT_PARAM&$SORT_TYPE=$sortType&" +
                "$PAGE_CONST=$page"


    suspend fun fetch(requestUrl: String): List<Book> =
        skrape(HttpFetcher) {
            request {
                timeout = DEFAULT_API_TIMEOUT
                url = requestUrl
                Log.d("URL REQUESt", url)
            }
            response {
                htmlDocument {
                    findAll("table").asSequence().drop(2).map {

                        val elementList =
                            tryOrNull {
                                it.findAll("tr").filter { it.children.size >= 2 }
                            }?.map { it.findAll("td") }?.flatten()

                        val res = if (!elementList.isNullOrEmpty()) {
                            elementList.mapNotNull {
                                elementList.toBook()
                            }
                        } else {
                            emptyList()
                        }

                        res
                    }.flatten().toSet().toList()
                }
            }
        }
}