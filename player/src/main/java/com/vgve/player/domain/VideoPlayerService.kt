 package com.vgve.player.domain

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.StateFlow

@UnstableApi
interface VideoPlayerService {

    val player: Player
    val playerState: StateFlow<PlayerModel>

    fun initPlayer()
    fun setMedia(isRepeatable: Boolean, link: String)
    fun pause()
    fun resume()
    fun release()
    fun mute()
    fun restoreSettings(position: Long, isReady: Boolean)
    fun rewind()
    fun forward()
    fun rewind(seekDuration: Int)
    fun forward(seekDuration: Int)
    fun setPlaybackSpeed(speed: Float)
    fun selectQuality(quality: VideoQuality)
}
