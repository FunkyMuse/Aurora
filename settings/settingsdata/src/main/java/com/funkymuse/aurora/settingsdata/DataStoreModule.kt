package com.funkymuse.aurora.settingsdata

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by funkymuse on 8/18/21 to long live and prosper !
 */
@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    private const val SETTINGS_NAME = "settings"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)

    @Provides
    @Singleton
    fun dataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

}