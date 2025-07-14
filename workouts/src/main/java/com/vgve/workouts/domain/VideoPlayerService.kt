package com.vgve.workouts.domain

import androidx.media3.exoplayer.ExoPlayer
import com.vgve.workouts.data.service.PlayerModel
import kotlinx.coroutines.flow.StateFlow

interface VideoPlayerService {

    val player: ExoPlayer
    val playerState: StateFlow<PlayerModel>

    fun initPlayer()
    fun setMedia(link: String)
    fun pause()
    fun resume()
    fun changeSpeed(speed: Float)
    fun clearAndStop()
    fun mute()
    fun restoreSettings(position: Long, isReady: Boolean)
    fun stop()
    fun rewind()
    fun forward()
    fun rewind(seekDuration: Int)
    fun forward(seekDuration: Int)
    fun onClickPlay()
}
