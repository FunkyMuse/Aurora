package com.funkymuse.aurora.mirrorsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.crazylegend.kotlinextensions.singleton.ParameterizedSingleton
import com.funkymuse.aurora.consts.MIRRORS_DB_NAME
import com.funkymuse.aurora.dto.ArrayListStringConverter

/**
 * Created by FunkyMuse, date 3/3/21
 */
@Database(entities = [MirrorModel::class], version = 1)
@TypeConverters(ArrayListStringConverter::class)
abstract class MirrorsDatabase : RoomDatabase() {
    abstract fun dao(): MirrorDao

    companion object : ParameterizedSingleton<MirrorsDatabase, Context>({
        Room.databaseBuilder(it, MirrorsDatabase::class.java, MIRRORS_DB_NAME)
            .build()
    })
}