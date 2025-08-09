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
import kotlinx.coroutines.flow.Flow
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

    private val _uiState = MutableStateFlow(WorkoutCardState())
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<WorkoutCardEffect> = Channel()
    val effect: Flow<WorkoutCardEffect> = _effect.receiveAsFlow()

    init {
        handleIntent(WorkoutCardIntent.InitialLoad)
    }

    fun handleIntent(intent: WorkoutCardIntent) {
        when (intent) {
            is WorkoutCardIntent.InitialLoad -> loadWorkoutData()
            is WorkoutCardIntent.PlayerAction -> handlePlayerAction(intent.action)
            is WorkoutCardIntent.SetPlaybackSpeed -> setSpeed(intent.speed)
            is WorkoutCardIntent.SetVideoQuality -> setQuality(intent.quality)
            is WorkoutCardIntent.RestorePlayer -> restorePlayer(intent.isReady)
        }
    }

    private fun handlePlayerAction(action: PlayerAction) {
        when (action) {
            PlayerAction.Play -> videoPlayerService.resume()
            PlayerAction.Pause -> videoPlayerService.pause()
            PlayerAction.Rewind -> videoPlayerService.rewind()
            PlayerAction.Forward -> videoPlayerService.forward()
            PlayerAction.Mute -> videoPlayerService.mute()
            PlayerAction.Replay -> videoPlayerService.replay()
        }
    }

    private fun loadWorkoutData() {
        launchSafe(
            errorHandler = { exception ->
                Log.e(TAG, "Error loading workout data", exception)
                viewModelScope.launch { _effect.send(WorkoutCardEffect.LoadFailed) }
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

    private fun restorePlayer(isReady: Boolean) {
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

    data class WorkoutCardState(
        val isLoading: Boolean = false,
        val workout: WorkoutModel? = null,
        val videoWorkout: VideoWorkoutModel? = null,
        val player: Player? = null,
        val playerState: PlayerModel? = null
    )

    sealed class WorkoutCardIntent {
        data object InitialLoad : WorkoutCardIntent()
        data class PlayerAction(val action: WorkoutCardViewModel.PlayerAction) : WorkoutCardIntent()
        data class SetPlaybackSpeed(val speed: Speed) : WorkoutCardIntent()
        data class SetVideoQuality(val quality: VideoQuality?) : WorkoutCardIntent()
        data class RestorePlayer(val isReady: Boolean) : WorkoutCardIntent()
    }

    enum class PlayerAction {
        Play, Pause, Rewind, Forward, Mute, Replay
    }

    sealed class WorkoutCardEffect {
        data object LoadFailed : WorkoutCardEffect()
    }
}
