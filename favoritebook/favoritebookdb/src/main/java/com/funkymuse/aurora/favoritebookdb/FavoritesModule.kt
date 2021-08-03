package com.funkymuse.aurora.favoritebookdb

import android.content.Context
import com.funkymuse.aurora.favoritebookdb.db.FavoritesDAO
import com.funkymuse.aurora.favoritebookdb.db.FavoritesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/27/21 to long live and prosper !
 */
@Module
@InstallIn(SingletonComponent::class)
object FavoritesModule {

    @Provides
    @Singleton
    fun favoritesDBDao(@ApplicationContext context: Context): FavoritesDAO =
        FavoritesDatabase.getInstance(context).dao()

}