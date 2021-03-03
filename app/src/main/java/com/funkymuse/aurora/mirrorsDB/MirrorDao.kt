package com.funkymuse.aurora.mirrorsDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

/**
 * Created by FunkyMuse, date 3/3/21
 */
@Dao
interface MirrorDao {

    @Query("select * from mirrors where id =:bookID limit 1")
    suspend fun getMirrorModelForBookId(bookID: Int): MirrorModel?

    @Insert(onConflict = REPLACE)
    suspend fun insertMirrorModel(mirrorModel: MirrorModel)

    @Query("delete from mirrors where id =:bookID")
    suspend fun deleteMirrorModel(bookID: Int)
}