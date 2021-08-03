package com.funkymuse.aurora.libgenapi

import com.funkymuse.aurora.serverconstants.LIBGEN_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/26/21 to long live and prosper !
 */
@Module
@InstallIn(SingletonComponent::class)
object LibgenModule {

    @Provides
    @Singleton
    fun libgenAPI(
        retrofit: Retrofit.Builder,
        httpClient: OkHttpClient.Builder
    ): LibgenAPI = retrofit
        .baseUrl(LIBGEN_BASE_URL)
        .client(httpClient.build())
        .build().create()
}