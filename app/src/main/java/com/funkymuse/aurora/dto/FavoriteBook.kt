package com.funkymuse.aurora.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters


/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Entity(tableName = "favoriteBooks")
data class FavoriteBook(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    override var title: String? = null,

    @ColumnInfo(name = "year")
    override var year: String? = null,

    @ColumnInfo(name = "pages")
    override var pages: String? = null,

    @ColumnInfo(name = "extension")
    override var extension: String? = null,

    @ColumnInfo(name = "author")
    override var author: String? = null,

    @ColumnInfo(name = "mirrors")
    @TypeConverters(ArrayListStringConverter::class)
    val mirrors: List<String>? = null
) : GeneralBook