package com.funkymuse.aurora.internetdetector

import android.content.Context
import com.crazylegend.internetdetector.InternetDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/26/21 to long live and prosper !
 */

@Module
@InstallIn(SingletonComponent::class)
object InternetDetectorModule {


    @Provides
    @Singleton
    fun internetDetector(@ApplicationContext context: Context) = InternetDetector(context)

}