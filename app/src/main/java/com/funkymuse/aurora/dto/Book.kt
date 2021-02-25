package com.funkymuse.aurora.dto

import com.crazylegend.kotlinextensions.tryOrNull
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode


/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */


data class Book(private val element: Element) {

    val generateFavoriteBook get() = FavoriteBook(id.toString(), title, year, pages, extension, mirrors)

    val id: String?
        get() = tryOrNull {
            (element.childNodes()[0].childNodes()[0] as TextNode).wholeText
        }

    var isInFavorites: Boolean = false

    val authors: String?
        get() = tryOrNull {
            (element.childNodes()[2].childNodes()[0].childNodes()[0] as TextNode).wholeText
        }


    val title: String?
        get() = tryOrNull {
            parseTitle(element)
        }


    val publisher: String?
        get() = tryOrNull {
            (element.childNodes()[6].childNodes()[0] as TextNode).wholeText
        }


    val year: String?
        get() = tryOrNull {
            (element.childNodes()[8].childNodes()[0] as TextNode).wholeText
        }


    val pages: String?
        get() = tryOrNull {
            (element.childNodes()[10].childNodes()[0] as TextNode).wholeText
        }


    val language: String?
        get() = tryOrNull {
            (element.childNodes()[12].childNodes()[0] as TextNode).wholeText
        }


    val size: String?
        get() = tryOrNull {
            (element.childNodes()[14].childNodes()[0] as TextNode).wholeText
        }


    val extension: String?
        get() = tryOrNull {
            (element.childNodes()[16].childNodes()[0] as TextNode).wholeText
        }


    val mirrors: ArrayList<String>?
        get() {
            return tryOrNull {
                val list: ArrayList<String> = ArrayList()
                for (i in 18..21) {
                    list.add(element.childNodes()[i].childNodes()[0].attributes().get("href"))
                }
                return list
            }
        }


    override fun toString(): String {
        return "$id $authors $title $publisher $year $language favorite links $mirrors"
    }

    /*
     * Handle special case where Title isn't the first link but instead there is some green text.
     */
    @Throws(IndexOutOfBoundsException::class)
    private fun parseTitle(element: Element): String {
        val elements = element.childNodes()[4].childNodes()
        return if (elements.size == 1) {
            (elements[0].childNodes()[0] as TextNode).wholeText
        } else {
            (elements[2].childNodes()[0] as TextNode).wholeText
        }
    }
}