package com.vgve.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    private fun setLevelInterceptor() =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

    @Provides
    @Singleton
    @QOkHttpClient
    fun provideOkHttpClient(
        @QHeaderInterceptor headerInterceptor: Interceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(setLevelInterceptor()))
        .addInterceptor(headerInterceptor)
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @QRetrofit
    fun provideRetrofit(
        @QOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }
}
