package com.vgve.player.data

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.vgve.player.domain.PlayerModel
import com.vgve.player.domain.VideoPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@UnstableApi
class VideoPlayerServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
): Player.Listener, VideoPlayerService {

    companion object {
        private const val SEEK_INTERVAL = 10000L
    }

    private var _player: ExoPlayer? = null
    override val player: ExoPlayer
        get() = requireNotNull(_player)

    private val _playerState = MutableStateFlow(PlayerModel())
    override val playerState = _playerState.asStateFlow()

    private val trackSelector = DefaultTrackSelector(context).apply {
        val parameters = buildUponParameters()
            .setMaxVideoSizeSd()
            .build()
        setParameters(parameters)
    }

    override fun initPlayer() {
        ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setSeekForwardIncrementMs(SEEK_INTERVAL)
            .setSeekBackIncrementMs(SEEK_INTERVAL)
            .build()
            .also {
                _player = it
            }
        _playerState.update {
            it.copy(
                isPlayWhenReady = player.playWhenReady
            )
        }
    }

    override fun setMedia(link: String) {
        val mediaItem = MediaItem.fromUri(link)
        player.apply {
            // repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(mediaItem)
            prepare()
        }
    }

    override fun pause() {
        player.pause()
        player.playWhenReady = false
        _playerState.update {
            it.copy(
                isPlayWhenReady = player.playWhenReady
            )
        }
    }

    override fun stop() {
        // onStop() in Fragment
        _playerState.update {
            it.copy(
                isPlayWhenReady = player.playWhenReady
            )
        }
        player.pause()
        player.playWhenReady = false
    }

    override fun resume() {
        player.play()
        player.playWhenReady = true

        _playerState.update {
            it.copy(
                isPlayWhenReady = player.playWhenReady
            )
        }
    }

    override fun changeSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    override fun rewind() {
        player.seekBack()
    }

    override fun forward() {
        player.seekForward()
    }

    override fun rewind(seekDuration: Int) {
        player.seekTo(
            player.currentMediaItemIndex,
            max(0, player.currentPosition - seekDuration)
        )
    }

    override fun forward(seekDuration: Int) {
        player.seekTo(
            player.currentMediaItemIndex,
            min(player.duration, player.currentPosition + seekDuration)
        )
    }

    override fun mute() {
        player.volume = if (isMute()) {
            1f
        } else {
            0f
        }
    }

    override fun restoreSettings(position: Long, isReady: Boolean) {
        player.seekTo(position)
        player.playWhenReady = isReady
    }

    private fun isMute() = player.volume == 0F

    override fun clearAndStop() {
        player.apply {
            clearMediaItems()
            release()
        }
    }
}
