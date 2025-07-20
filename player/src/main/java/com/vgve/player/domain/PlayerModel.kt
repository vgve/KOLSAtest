package com.vgve.player.domain

data class PlayerModel(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val duration: Long = 0,
    val availableQualities: List<VideoQuality> = emptyList(),
    val selectedQuality: VideoQuality = VideoQuality.Auto,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class VideoQuality {
    data object Auto : VideoQuality()
    data class Specific(val width: Int, val height: Int) : VideoQuality()
}

enum class PlayerState {
    IDLE, BUFFERING, READY, ENDED
}
