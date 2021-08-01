package com.funkymuse.aurora.favoritebookmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.funkymuse.aurora.generalbook.GeneralBook


/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Entity(tableName = "favorite_books")
data class FavoriteBook(

    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String = "",

    @ColumnInfo(name = "title")
    override val title: String? = null,

    @ColumnInfo(name = "image")
    override val image:String?=null,

    @ColumnInfo(name = "author")
    override val author: String? = null,

) : GeneralBook