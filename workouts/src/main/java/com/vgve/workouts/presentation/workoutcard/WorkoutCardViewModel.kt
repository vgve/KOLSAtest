package com.vgve.workouts.presentation.workoutcard

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.vgve.player.domain.PlayerModel
import com.vgve.player.domain.Speed
import com.vgve.player.domain.VideoPlayerService
import com.vgve.player.domain.VideoQuality
import com.vgve.workouts.domain.models.VideoWorkoutModel
import com.vgve.workouts.domain.models.WorkoutModel
import com.vgve.workouts.domain.usecases.GetWorkoutVideoUseCase
import com.vgve.workouts.presentation.utils.extensions.launchSafe
import com.vgve.workouts.presentation.utils.extensions.track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@UnstableApi
class WorkoutCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getWorkoutVideoUseCase: GetWorkoutVideoUseCase,
    private val videoPlayerService: VideoPlayerService
) : ViewModel() {

    companion object {
        const val KEY_ARGS_WORKOUT = "args_workout"
    }

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _uiAction: Channel<UIAction> = Channel()
    val uiAction = _uiAction.receiveAsFlow()

    init {
        initScreen()
    }

    fun initScreen() {
        launchSafe(
            errorHandler = { exception ->
                Log.e(TAG, "$exception")
                viewModelScope.launch { _uiAction.send(UIAction.OnFailure) }
            },
        ) {
            val workout = savedStateHandle.get<WorkoutModel>(KEY_ARGS_WORKOUT)
            val videoWorkout = workout?.id?.let { getWorkoutVideoUseCase.invoke(it) }

            videoPlayerService.initPlayer()
            _uiState.update {
                it.copy(
                    workout = workout,
                    videoWorkout = videoWorkout,
                    player = videoPlayerService.player
                )
            }
            uiState.value.videoWorkout?.link?.let {
                videoPlayerService.setMedia(false, it)
            }
            observeVideoPlayer(videoPlayerService.playerState)
        }.track { _uiState.update { uiState -> uiState.copy(isLoading = it) } }
    }

    private fun observeVideoPlayer(player: StateFlow<PlayerModel>) {
        viewModelScope.launch {
            player.collect { player ->
                _uiState.update { it.copy(playerState = player) }
            }
        }
    }

    fun onRewind() = videoPlayerService.rewind()
    fun onForward() = videoPlayerService.forward()
    fun onPlay() = videoPlayerService.resume()
    fun onPause() = videoPlayerService.pause()
    fun onMute() = videoPlayerService.mute()
    fun onReplay() = videoPlayerService.replay()

    fun onRestore(isReady: Boolean) {
        videoPlayerService.restore(isReady)
    }

    fun setSpeed(speed: Speed) = videoPlayerService.setPlaybackSpeed(speed)

    fun setQuality(quality: VideoQuality?) {
        quality?.let { videoPlayerService.selectQuality(it) }
    }

    override fun onCleared() {
        super.onCleared()
        videoPlayerService.release()
    }

    data class UIState(
        val isLoading: Boolean = false,
        val workout: WorkoutModel? = null,
        val videoWorkout: VideoWorkoutModel? = null,
        val player: Player? = null,
        val playerState: PlayerModel? = null
    )

    sealed class UIAction {
        data object OnFailure : UIAction()
    }
}
