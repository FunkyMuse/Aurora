package com.funkymuse.aurora.favoritebookdb.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.crazylegend.kotlinextensions.singleton.ParameterizedSingleton
import com.funkymuse.aurora.favoritebookmodel.ArrayListStringConverter
import com.funkymuse.aurora.favoritebookmodel.FavoriteBook


/**
 * Created by FunkyMuse, date 3/3/21
 */

const val FAVORITES_DB_NAME = "favorites-db"


@Database(entities = [FavoriteBook::class], version = 2, exportSchema = false)
@TypeConverters(ArrayListStringConverter::class)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun dao(): FavoritesDAO

    companion object : ParameterizedSingleton<FavoritesDatabase, Context>({
        Room.databaseBuilder(it, FavoritesDatabase::class.java, FAVORITES_DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    })
}