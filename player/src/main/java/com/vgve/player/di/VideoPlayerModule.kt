package com.vgve.player.di

import com.vgve.player.data.VideoPlayerServiceImpl
import com.vgve.player.domain.VideoPlayerService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
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
