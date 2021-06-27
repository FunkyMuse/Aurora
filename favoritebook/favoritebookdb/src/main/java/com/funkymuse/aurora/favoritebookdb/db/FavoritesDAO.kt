package com.funkymuse.aurora.favoritebookdb.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Created by FunkyMuse, date 3/3/21
 */

@Dao
interface FavoritesDAO {

    @Query("select * from favoriteBooks")
    fun getAllFavorites(): PagingSource<Int, com.funkymuse.aurora.favoritebookmodel.FavoriteBook>

    @Delete
    suspend fun deleteFromFavorites(favoriteBook: com.funkymuse.aurora.favoritebookmodel.FavoriteBook)

    @Query("delete from favoriteBooks where id =:favID")
    suspend fun deleteFromFavoritesByID(favID: Int)

    @Insert(onConflict = REPLACE)
    suspend fun insertIntoFavorites(favoriteBook: com.funkymuse.aurora.favoritebookmodel.FavoriteBook)

    @Query("select * from favoriteBooks where id =:bookID limit 1")
    fun getFavoriteById(bookID: Int): Flow<com.funkymuse.aurora.favoritebookmodel.FavoriteBook?>
}