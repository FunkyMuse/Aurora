package com.funkymuse.aurora.di

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import com.crazylegend.internetdetector.InternetDetector
import com.crazylegend.retrofit.RetrofitClient
import com.crazylegend.retrofit.adapter.RetrofitResultAdapterFactory
import com.crazylegend.retrofit.interceptors.ConnectivityInterceptor
import com.crazylegend.toaster.Toaster
import com.funkymuse.aurora.BuildConfig
import com.funkymuse.aurora.R
import com.funkymuse.aurora.api.LibgenAPI
import com.funkymuse.aurora.favorites.db.FavoritesDAO
import com.funkymuse.aurora.favorites.db.FavoritesDatabase
import com.funkymuse.aurora.serverconstants.LIBGEN_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * Created by FunkyMuse, date 2/28/21
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun imageLoader(@ApplicationContext context: Context): ImageLoader = ImageLoader.Builder(context)
            .error(R.drawable.ic_logo)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.DISABLED)
            .fallback(R.drawable.ic_logo)
            .build()

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

    @Provides
    @Singleton
    fun favoritesDBDao(@ApplicationContext context: Context): FavoritesDAO =
        FavoritesDatabase.getInstance(context).dao()

    @Provides
    @Singleton
    fun internetDetector(@ApplicationContext context: Context) = InternetDetector(context)

    @Provides
    @Singleton
    fun toaster(@ApplicationContext context: Context) = Toaster(context)
}