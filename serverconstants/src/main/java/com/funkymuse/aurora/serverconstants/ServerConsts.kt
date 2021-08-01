package com.funkymuse.aurora.serverconstants


/**
 * Created by FunkyMuse on 10/21/19 to long live and prosper !
 */

private const val BASE_URL = "https://libgen.is/"
const val SEARCH_BASE_URL = "${BASE_URL}search.php"
const val DEFAULT_API_TIMEOUT = 10000
const val RES_CONST = "res"
const val REQ_CONST = "req"
const val PAGE_CONST = "page"
const val PAGE_SIZE = "100"
const val LIBGEN_COVER_IMAGE_URL = "${BASE_URL}covers/"

const val LIBGEN_BASE_URL = "http://gen.lib.rus.ec/"

const val JSON_PHP_PAGE_CONST = "json.php"

const val IDS_QUERY_CONST = "ids"
const val FIELDS_QUERY_CONST = "fields"
const val FIELDS_QUERY = "*"

const val SORT_QUERY = "sort"
const val SORT_TYPE = "sortmode"

const val VIEW_QUERY = "view"
const val VIEW_QUERY_PARAM = "detailed"

const val SEARCH_WITH_MASK = "phrase"
const val SEARCH_WITH_MASK_YES = "0"
const val SEARCH_WITH_MASK_NO = "1"

const val COLUM_QUERY = "column"
const val FIELD_DEFAULT_PARAM = "def"
const val FIELD_TITLE_PARAM = "title"
const val FIELD_AUTHOR_PARAM = "author"
const val FIELD_SERIES_PARAM = "series"
const val FIELD_PUBLISHER_PARAM = "publisher"
const val FIELD_YEAR_PARAM = "year"
const val FIELD_ISBN_PARAM = "identifier"
const val FIELD_LANGUAGE_PARAM = "language"
const val FIELD_MD5_PARAM = "md5"
const val FIELD_TAGS_PARAM = "tags"
const val FIELD_EXTENSION_PARAM = "extension"

fun getFieldParamByPosition(position: Int) = when (position) {
    0 -> FIELD_DEFAULT_PARAM
    1 -> FIELD_TITLE_PARAM
    2 -> FIELD_AUTHOR_PARAM
    3 -> FIELD_SERIES_PARAM
    4 -> FIELD_PUBLISHER_PARAM
    5 -> FIELD_YEAR_PARAM
    6 -> FIELD_ISBN_PARAM
    7 -> FIELD_LANGUAGE_PARAM
    8 -> FIELD_MD5_PARAM
    9 -> FIELD_TAGS_PARAM
    10 -> FIELD_EXTENSION_PARAM
    else -> FIELD_DEFAULT_PARAM
}

const val SORT_TYPE_ASC = "ASC"
const val SORT_TYPE_DESC = "DESC"
const val SORT_YEAR_CONST = "year"
const val SORT_AUTHOR = "author"
const val SORT_TITLE = "title"
const val SORT_SIZE = "filesize"
const val SORT_EXTENSION = "extension"
const val SORT_PUBLISHER = "publisher"


const val LAST_MODE = "mode"
const val LAST_QUERY = "last"

fun torrentDownloadURL(md5: String) = "${BASE_URL}book/index.php?md5=$md5&oftorrent="