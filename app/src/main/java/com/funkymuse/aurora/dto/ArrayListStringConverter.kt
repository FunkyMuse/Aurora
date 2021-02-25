package com.funkymuse.aurora.dto

import androidx.room.TypeConverter
import com.crazylegend.moshi.fromJson
import com.crazylegend.moshi.toJson
import com.crazylegend.moshi.toJsonObjectArrayList


/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

class ArrayListStringConverter {
    @TypeConverter
    fun toString(list: ArrayList<String>?): String? =
         list?.toJson()


    @TypeConverter
    fun toList(json: String?): ArrayList<String>? =
         json?.toJsonObjectArrayList()

}