package com.funkymuse.aurora.mirrorsDB

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Hristijan, date 3/5/21
 */
@Singleton
class MirrorsRepository @Inject constructor(private val mirrorDao: MirrorDao) : MirrorDao by mirrorDao {

    suspend fun saveMirrorsForBookId(id: String?, mirrors: List<String>?) {
        if (id?.toIntOrNull() == null || mirrors.isNullOrEmpty()) return
        mirrorDao.insertMirrorModel(MirrorModel(id.toInt(), mirrors))
    }
}