package com.funkymuse.aurora.networking

import com.crazylegend.retrofit.adapter.RetrofitResultAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/26/21 to long live and prosper !
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    private const val TIMEOUT_NAMED = "timeoutName"
    private const val TIME_UNIT_NAMED = "timeUnitNamed"

    @Provides
    @Named(TIMEOUT_NAMED)
    @Singleton
    fun timeOut(): Long = 10L

    @Provides
    @Named(TIME_UNIT_NAMED)
    @Singleton
    fun timeOutUnit(): TimeUnit = TimeUnit.SECONDS

    @Provides
    @Singleton
    fun moshiConverter(): MoshiConverterFactory = MoshiConverterFactory.create()

    @Provides
    @Singleton
    fun retrofitResultAdapter(): RetrofitResultAdapterFactory = RetrofitResultAdapterFactory()

    @Provides
    @Singleton
    fun okHttpClientBuilder(
        interceptorsSet: Set<@JvmSuppressWildcards Interceptor>,
        @Named(TIMEOUT_NAMED) timeOut: Long,
        @Named(TIME_UNIT_NAMED) timeOutUnit: TimeUnit
    ): OkHttpClient.Builder = OkHttpClient.Builder().also { client ->
        interceptorsSet.forEach { client.addInterceptor(it) }
        client.callTimeout(timeOut, timeOutUnit)
    }

    @Provides
    @Singleton
    fun retrofitBuilder(
        retrofitResultAdapterFactory: RetrofitResultAdapterFactory,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit.Builder = Retrofit.Builder()
        .addCallAdapterFactory(retrofitResultAdapterFactory)
        .addConverterFactory(moshiConverterFactory)
}