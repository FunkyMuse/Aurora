package com.funkymuse.aurora.consts



/**
 * Created by FunkyMuse on 10/22/19 to long live and prosper !
 */

const val MIRRORS_DB_NAME = "mirrors-db"
const val FAVORITES_DB_NAME = "favorites-db"
fun torrentDownloadURL(md5: String) = "https://libgen.is/book/index.php?md5=$md5&oftorrent="