package com.funkymuse.aurora.bottomNav.favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.funkymuse.aurora.dto.FavoriteBook
import kotlinx.coroutines.flow.Flow


/**
 * Created by FunkyMuse, date 3/3/21
 */

@Dao
interface FavoritesDAO {

    @Query("select * from favoriteBooks")
    fun getAllFavorites(): Flow<FavoriteBook>

    @Delete
    suspend fun deleteFromFavorites(favoriteBook: FavoriteBook)

    @Query("delete from favoriteBooks where id =:favID")
    suspend fun deleteFromFavoritesByID(favID: Int)

    @Insert(onConflict = REPLACE)
    suspend fun insertIntoFavorites(favoriteBook: FavoriteBook)

    @Query("select * from favoriteBooks where id =:bookID limit 1")
    fun getFavoriteById(bookID:Int) : Flow<FavoriteBook?>
}