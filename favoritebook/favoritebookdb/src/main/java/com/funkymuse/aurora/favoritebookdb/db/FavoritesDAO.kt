package com.funkymuse.aurora.favoritebookdb.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.funkymuse.aurora.favoritebookmodel.FavoriteBook
import kotlinx.coroutines.flow.Flow


/**
 * Created by FunkyMuse, date 3/3/21
 */

@Dao
interface FavoritesDAO {

    @Query("select * from favorite_books")
    fun getAllFavorites(): PagingSource<Int, FavoriteBook>

    @Query("select count(*) from favorite_books")
    fun favoriteItemsCount(): Flow<Int>

    @Delete
    suspend fun deleteFromFavorites(favoriteBook: FavoriteBook)

    @Query("delete from favorite_books where id =:favID")
    suspend fun deleteFromFavoritesByID(favID: String)

    @Insert(onConflict = REPLACE)
    suspend fun insertIntoFavorites(favoriteBook: FavoriteBook)

    @Query("select * from favorite_books where id GLOB '*' || :bookID|| '*' limit 1")
    fun getFavoriteById(bookID: String): Flow<FavoriteBook?>
}