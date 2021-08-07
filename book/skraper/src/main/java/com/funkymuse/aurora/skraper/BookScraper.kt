package com.funkymuse.aurora.skraper

import com.crazylegend.common.tryOrNull
import com.funkymuse.aurora.bookmodel.Book
import com.funkymuse.aurora.extensions.removeBrackets
import com.funkymuse.aurora.serverconstants.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 8/2/21 to long live and prosper !
 */
@Singleton
class BookScraper @Inject constructor() {

    private fun List<Element>.toBook(): Book? {
        val id = tryOrNull {
            this[2].select("a").firstOrNull()?.attr("href")?.substringAfter("md5=")
        } ?: return null
        val extension = tryOrNull { this[33].text() }?.uppercase()
        val size = tryOrNull { this[31].text() }?.substringBefore("(")?.trim()
        val pages = tryOrNull { this[21].text() }?.removeBrackets()?.substringBefore(" ")
        val image = tryOrNull {
            this[0].select("img").firstOrNull()?.attr("src")
        }
        val title = tryOrNull { this[2].text() }
        val author = tryOrNull { this[5].text() }
        val year = tryOrNull { this[15].text() }
        return Book(image, title, author, id, extension, pages, size, year)
    }

    fun generateSearchDataUrl(
        page: Int, searchQuery: String, sortQuery: String, sortType: String,
        searchInFieldsPosition: Int, maskWord: Boolean, connection: Connection
    ): Connection {
        connection.apply {
            timeout(DEFAULT_API_TIMEOUT)
            data(REQ_CONST, searchQuery)
            data(VIEW_QUERY, VIEW_QUERY_PARAM)
            data(COLUM_QUERY, getFieldParamByPosition(searchInFieldsPosition))
            data(SEARCH_WITH_MASK, if (maskWord) SEARCH_WITH_MASK_YES else SEARCH_WITH_MASK_NO)
            data(RES_CONST, PAGE_SIZE)
            data(SORT_QUERY, sortQuery)
            data(SORT_TYPE, sortType)
            data(PAGE_CONST, page.toString())
        }
        return connection
    }

    fun generateLatestBooksUrl(
        page: Int,
        sortQuery: String,
        sortType: String,
        connection: Connection
    ): Connection {
        connection.apply {
            timeout(DEFAULT_API_TIMEOUT)
            data(SORT_QUERY, sortQuery)
            data(VIEW_QUERY, VIEW_QUERY_PARAM)
            data(LAST_MODE, LAST_QUERY)
            data(COLUM_QUERY, FIELD_DEFAULT_PARAM)
            data(RES_CONST, PAGE_SIZE)
            data(SORT_TYPE, sortType)
            data(PAGE_CONST, page.toString())
        }
        return connection
    }


    fun fetch(connectionCallback: Connection.() -> Connection): List<Book> {
        val jsoup = Jsoup.connect(SEARCH_BASE_URL)
            .timeout(DEFAULT_API_TIMEOUT)
            .connectionCallback()
        val document = jsoup.get()
        return processDocument(document)
    }

    private fun processDocument(document: Document): List<Book> {
        return document.select("table").asSequence().drop(2).map {

            val elementList = tryOrNull { it.select("tr").filter { it.children().size >= 2 } }
                ?.map { it.select("td") }?.flatten()

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


