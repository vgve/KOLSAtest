package com.vgve.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QHeaderInterceptor
