package com.vgve.core.di

import com.vgve.core.BuildConfig
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

    private fun setLevelInterceptor() =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

    @Provides
    @Singleton
    @QHeaderInterceptor
    fun provideHeaderInterceptor() = Interceptor { chain ->
        val builder = chain.request().newBuilder().also { builder ->
            // can add tokens
            builder.header("Content-Type", "application/json")
        }
        return@Interceptor chain.proceed(builder.build())
    }

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
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
