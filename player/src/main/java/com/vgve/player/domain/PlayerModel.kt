package com.vgve.player.domain

data class PlayerModel(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val bufferedPosition: Long = 0,
    val duration: Long = 0,
    val availableQualities: List<VideoQuality> = emptyList(),
    val selectedQuality: VideoQuality = VideoQuality.Auto,
    val isLoading: Boolean = false,
    val isMute: Boolean = false,
    val isEnded: Boolean = false,
    val error: String? = null
)

sealed class VideoQuality {
    data object Auto : VideoQuality()
    data class Specific(val width: Int, val height: Int) : VideoQuality()
}

enum class Speed(val value: Float) {
    SLOW(0.5f), NORMAL(1f), FAST(1.25f), FASTER(1.5f), FASTEST(2f)
}

enum class SettingsType {
    QUALITY, SPEED, SUBTITLES, LANGUAGE
}
