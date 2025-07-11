package com.vgve.workouts.di

import com.vgve.core.di.QRetrofit
import com.vgve.workouts.data.repository.WorkoutsRepositoryImpl
import com.vgve.workouts.data.service.WorkoutsService
import com.vgve.workouts.domain.repository.WorkoutsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkoutsModule {

    @Provides
    @Singleton
    fun provideAuthService(
        @QRetrofit retrofit: Retrofit
    ): WorkoutsService = retrofit.create(WorkoutsService::class.java)

    @Provides
    @Singleton
    fun provideWorkoutsRepository(
        service: WorkoutsService
    ): WorkoutsRepository = WorkoutsRepositoryImpl(service)
}
