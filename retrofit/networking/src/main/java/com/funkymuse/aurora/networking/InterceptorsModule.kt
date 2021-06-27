package com.funkymuse.aurora.networking

import android.content.Context
import com.crazylegend.retrofit.interceptors.ConnectivityInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/26/21 to long live and prosper !
 */
@Module
@InstallIn(SingletonComponent::class)
object InterceptorsModule {
    @Provides
    @Singleton
    @IntoSet
    fun connectivityInterceptor(@ApplicationContext context: Context) =
            ConnectivityInterceptor(context)

    @Provides
    @Singleton
    @IntoSet
    fun loggingInterceptor(): Interceptor = HttpLoggingInterceptor().also {
        it.level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }
}