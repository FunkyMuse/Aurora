package com.funkymuse.aurora.toaster

import android.content.Context
import com.crazylegend.toaster.Toaster
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
object ToasterModule {

    @Provides
    @Singleton
    fun toaster(@ApplicationContext context: Context) : Toaster = Toaster(context)
}