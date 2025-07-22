package com.vgve.player.data

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.vgve.core.di.MainScope
import com.vgve.player.domain.PlayerModel
import com.vgve.player.domain.Speed
import com.vgve.player.domain.VideoPlayerService
import com.vgve.player.domain.VideoQuality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@UnstableApi
class VideoPlayerServiceImpl @Inject constructor(
    @MainScope private val coroutineScope: CoroutineScope,
    @ApplicationContext private val context: Context
): VideoPlayerService {

    companion object {
        private const val SEEK_INTERVAL = 10000L
        private const val PLAYER_STATE_UPDATE_INTERVAL = 250L
    }

    private var updateStateJob: Job? = null
    private var _player: ExoPlayer? = null

    override val player: Player
        get() = requireNotNull(_player) { "Player not initialized" }

    private val _playerState = MutableStateFlow(PlayerModel())
    override val playerState: StateFlow<PlayerModel> = _playerState.asStateFlow()

    private val trackSelector = DefaultTrackSelector(context).apply {
        val parameters = buildUponParameters()
            .setMaxVideoSizeSd()
            .build()
        setParameters(parameters)
    }

    private val listener = object : Player.Listener {
        override fun onPlayerErrorChanged(error: PlaybackException?) {
            _playerState.update { it.copy(error = error?.message) }
        }

        override fun onPlaybackStateChanged(state: Int) {
            updateStateJob?.cancel()
            when (state) {
                Player.STATE_READY -> {
                    updateStateJob = coroutineScope.launch {
                        while (true) {
                            _playerState.update {
                                it.copy(
                                    currentPosition = player.currentPosition,
                                    bufferedPosition = player.bufferedPosition,
                                    isPlaying = player.isPlaying,
                                    duration = player.duration,
                                    isLoading = false,
                                    isMute = isMute(),
                                    isEnded = false
                                )
                            }
                            delay(PLAYER_STATE_UPDATE_INTERVAL)
                        }
                    }
                }
                Player.STATE_ENDED -> {
                    player.playWhenReady = false
                    _playerState.update { it.copy(isPlaying = false, isEnded = true) }
                }
                Player.STATE_BUFFERING -> {
                    _playerState.update { it.copy(isLoading = true) }
                }
                Player.STATE_IDLE -> { }
            }
        }
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

        player.addListener(listener)
        _playerState.update {
            it.copy(
                availableQualities = getResolutions()
            )
        }
    }

    override fun setMedia(isRepeatable: Boolean, link: String) {
        val mediaItem = MediaItem.fromUri(link)
        player.apply {
            repeatMode = if (isRepeatable) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            setMediaItem(mediaItem)
            prepare()
        }
    }

    override fun pause() {
        player.pause()
        player.playWhenReady = false
    }

    override fun resume() {
        player.play()
        player.playWhenReady = true
    }

    override fun replay() {
        player.seekTo(0)
        resume()
    }

    override fun rewind() = player.seekBack()

    override fun forward() = player.seekForward()

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

    override fun setPlaybackSpeed(speed: Speed) = player.setPlaybackSpeed(speed.value)

    private fun isMute() = player.volume == 0F
    override fun mute() {
        player.volume = if (isMute()) 1f else 0f
    }

    override fun restore(isReady: Boolean) {
        player.playWhenReady = isReady
    }

    override fun release() {
        _playerState.update { PlayerModel() }
        player.removeListener(listener)
        player.release()
        _player = null
        updateStateJob?.cancel()
    }

    override fun selectQuality(quality: VideoQuality) {
        val params = trackSelector.buildUponParameters().apply {
            when (quality) {
                is VideoQuality.Specific -> setMaxVideoSize(quality.width, quality.height)
                is VideoQuality.Auto -> setMaxVideoSizeSd()
            }
        }.build()

        trackSelector.parameters = params
        _playerState.update { it.copy(selectedQuality = quality) }
    }

    private fun getResolutions(): List<VideoQuality> {
        val resolutions = mutableListOf<VideoQuality>()
        // val latch = CountDownLatch(1)

        val tracks = player.currentTracks
        for (group in tracks.groups) {
            if (group.type == C.TRACK_TYPE_VIDEO) {
                for (i in 0 until group.length) {
                    val format = group.getTrackFormat(i)
                    val width = format.width
                    val height = format.height
                    val bitrate = format.bitrate
                    if (width > 0 && height > 0) {
                        val kbps = bitrate / 1000
                        resolutions.add(VideoQuality.Specific(width, height))
                    }
                }
            }
        }
        return resolutions
    }
}
