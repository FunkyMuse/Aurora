package com.funkymuse.aurora.mirrorsDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.funkymuse.aurora.dto.ArrayListStringConverter

/**
 * Created by FunkyMuse, date 3/3/21
 */
@Entity(tableName = "mirrors")
data class MirrorModel(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val bookID: Int = 0,
    @ColumnInfo(name = "mirrors")
    @TypeConverters(ArrayListStringConverter::class)
    val mirrors: List<String>?
)