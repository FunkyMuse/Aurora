package com.funkymuse.aurora.bookmodel

import com.funkymuse.aurora.generalbook.GeneralBook
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode


/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */
class Book(private val element: Element) : GeneralBook {


    val id: String?
        get() = tryOrNull {
            (element.childNodes()[0].childNodes()[0] as TextNode).wholeText
        }

    override val author: String?
        get() = tryOrNull {
            (element.childNodes()[2].childNodes()[0].childNodes()[0] as TextNode).wholeText
        }


    override val title: String?
        get() = tryOrNull {
            parseTitle(element)
        }


    val publisher: String?
        get() = tryOrNull {
            (element.childNodes()[6].childNodes()[0] as TextNode).wholeText
        }


    override val year: String?
        get() = tryOrNull {
            (element.childNodes()[8].childNodes()[0] as TextNode).wholeText
        }


    override val pages: String?
        get() = tryOrNull {
            (element.childNodes()[10].childNodes()[0] as TextNode).wholeText
        }


    private val language: String?
        get() = tryOrNull {
            (element.childNodes()[12].childNodes()[0] as TextNode).wholeText
        }


    private val size: String?
        get() = tryOrNull {
            (element.childNodes()[14].childNodes()[0] as TextNode).wholeText
        }


    override val extension: String?
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
                list
            }
        }

    override fun equals(other: Any?): Boolean {
        if (other !is Book) return false
        return other.id == id
    }

    override fun toString(): String {
        return "$id $author $title $publisher $year $language favorite links $mirrors"
    }

    private fun <T> tryOrNull(action: () -> T): T? {
        return try {
            action()
        } catch (t: Throwable) {
            return null
        }
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

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (publisher?.hashCode() ?: 0)
        result = 31 * result + (year?.hashCode() ?: 0)
        result = 31 * result + (pages?.hashCode() ?: 0)
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + (size?.hashCode() ?: 0)
        result = 31 * result + (extension?.hashCode() ?: 0)
        result = 31 * result + (mirrors?.hashCode() ?: 0)
        return result
    }


}