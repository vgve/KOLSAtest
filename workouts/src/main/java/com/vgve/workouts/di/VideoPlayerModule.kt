package com.vgve.workouts.di

import com.vgve.workouts.data.service.VideoPlayerServiceImpl
import com.vgve.workouts.domain.VideoPlayerService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface VideoPlayerModule {

    @Binds
    @Singleton
    fun bindVideoPlayerService(
        impl: VideoPlayerServiceImpl
    ): VideoPlayerService
}
