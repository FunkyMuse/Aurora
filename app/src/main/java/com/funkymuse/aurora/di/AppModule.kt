package com.funkymuse.aurora.di

import android.content.Context
import com.crazylegend.retrofit.RetrofitClient
import com.crazylegend.retrofit.adapter.RetrofitResultAdapterFactory
import com.crazylegend.retrofit.interceptors.ConnectivityInterceptor
import com.funkymuse.aurora.BuildConfig
import com.funkymuse.aurora.api.LibgenAPI
import com.funkymuse.aurora.consts.LIBGEN_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * Created by Hristijan, date 2/28/21
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun retrofitAdapterFactory() = RetrofitResultAdapterFactory()

    @Provides
    @Singleton
    fun moshiConverter() = MoshiConverterFactory.create()

    @Provides
    @Singleton
    fun libgenApi(
        @ApplicationContext context: Context,
        moshiConverterFactory: MoshiConverterFactory,
        retrofitResultAdapterFactory: RetrofitResultAdapterFactory
    ) = RetrofitClient.customInstance(LIBGEN_BASE_URL, BuildConfig.DEBUG, {
        addInterceptor(ConnectivityInterceptor(context))
    }) {
        addCallAdapterFactory(retrofitResultAdapterFactory)
        addConverterFactory(moshiConverterFactory)
    }.create<LibgenAPI>()
}