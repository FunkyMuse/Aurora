package com.funkymuse.aurora.appscope

import com.funkymuse.aurora.dispatchers.DefaultDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/27/21 to long live and prosper !
 */
@InstallIn(SingletonComponent::class)
@Module
object AppScopeModule {

    @Singleton
    @Provides
    @ApplicationScope
    fun providesCoroutineScope(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher): CoroutineScope =
            CoroutineScope(SupervisorJob() + defaultDispatcher)

}