package com.funkymuse.aurora.dto

import androidx.room.TypeConverter
import com.crazylegend.moshi.toJson
import com.crazylegend.moshi.toJsonObjectList


/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

class ArrayListStringConverter {
    @TypeConverter
    fun toString(list: List<String>?): String? =
        list?.toJson()


    @TypeConverter
    fun toList(json: String?): List<String>? =
        json?.toJsonObjectList()

}