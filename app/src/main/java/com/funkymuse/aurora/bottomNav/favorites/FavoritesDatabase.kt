package com.funkymuse.aurora.bottomNav.favorites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.crazylegend.kotlinextensions.singleton.ParameterizedSingleton
import com.funkymuse.aurora.consts.FAVORITES_DB_NAME
import com.funkymuse.aurora.dto.ArrayListStringConverter
import com.funkymuse.aurora.dto.FavoriteBook


/**
 * Created by FunkyMuse, date 3/3/21
 */


@Database(entities = [FavoriteBook::class], version = 1, exportSchema = false)
@TypeConverters(ArrayListStringConverter::class)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun dao(): FavoritesDAO

    companion object : ParameterizedSingleton<FavoritesDatabase, Context>({
        Room.databaseBuilder(it, FavoritesDatabase::class.java, FAVORITES_DB_NAME)
            .build()
    })
}