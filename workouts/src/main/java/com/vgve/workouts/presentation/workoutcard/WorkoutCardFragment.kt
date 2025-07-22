package com.vgve.workouts.presentation.workoutcard

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.vgve.player.domain.SettingsType
import com.vgve.player.presentation.ui.showQualityPopup
import com.vgve.player.presentation.ui.showSettingsPopup
import com.vgve.player.presentation.ui.showSpeedPopup
import com.vgve.workouts.R
import com.vgve.workouts.databinding.FragmentWorkoutCardBinding
import com.vgve.workouts.domain.models.toResString
import com.vgve.workouts.presentation.utils.extensions.collectOnStarted
import com.vgve.workouts.presentation.utils.extensions.isInteger
import com.vgve.workouts.presentation.utils.extensions.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint @UnstableApi
class WorkoutCardFragment: Fragment(R.layout.fragment_workout_card) {

    companion object {
        const val KEY_PLAYER_PLAY_WHEN_READY = "key_play_when_ready"
    }

    private val binding: FragmentWorkoutCardBinding by viewBinding(FragmentWorkoutCardBinding::bind)
    private val viewModel: WorkoutCardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUIState()
        observeUIAction()
    }

    private fun observeUIState() = viewModel.uiState.onEach { uiState ->
        with(binding) {
            // Toolbar
            toolbar.apply {
                ivBack.setOnClickListener {
                    findNavController().popBackStack()
                }
                tvToolbarTitle.text = uiState.workout?.title
            }

            // Info
            tvDescription.apply {
                isVisible = !uiState.workout?.description.isNullOrEmpty()
                text = uiState.workout?.description
            }
            tvType.text = uiState.workout?.type?.toResString()?.let { getString(it) }
            tvDuration.apply {
                isVisible = uiState.videoWorkout?.duration?.isInteger() ?: false
                text = getString(R.string.duration_minutes, uiState.videoWorkout?.duration)
            }

            // Progress bar
            clMain.isVisible = !uiState.isLoading
            pbWorkoutCard.isVisible = uiState.isLoading

            // Player
            pvWorkout.apply {
                isVisible = !uiState.isLoading
                uiState.player?.let {
                    setPlayer(it)
                }
            }

            // Custom controllers
            pvWorkout.play.apply {
                pvWorkout.setPlayResource(uiState.playerState?.isPlaying == true)
                isVisible = uiState.playerState?.isEnded == false
                setOnClickListener {
                    if (uiState.playerState?.isPlaying == true) viewModel.onPause()
                    else viewModel.onPlay()
                }
            }
            pvWorkout.replay.apply {
                isVisible = uiState.playerState?.isEnded == true
                setOnClickListener {
                    viewModel.onReplay()
                }
            }
            pvWorkout.mute.apply {
                isEnabled = uiState.playerState?.isEnded == false
                pvWorkout.setMuteResource(uiState.playerState?.isMute == true)
                setOnClickListener {
                    viewModel.onMute()
                }
            }
            pvWorkout.forward.apply {
                isVisible = uiState.playerState?.isEnded == false
                setOnClickListener {
                    viewModel.onForward()
                }
            }
            pvWorkout.rewind.apply {
                isVisible = uiState.playerState?.isEnded == false
                setOnClickListener {
                    viewModel.onRewind()
                }
            }
            pvWorkout.settings.setOnClickListener {
                showSettingsPopup(
                    view = pvWorkout.settings,
                    onClick = { type ->
                        when(type) {
                            SettingsType.QUALITY -> {
                                showQualityPopup(
                                    view = pvWorkout.settings,
                                    resolutions = uiState.playerState?.availableQualities,
                                    onClick = {
                                        viewModel.setQuality(it)
                                    }
                                )
                            }
                            SettingsType.SPEED -> {
                                showSpeedPopup(
                                    view = pvWorkout.settings,
                                    onClick = {
                                        viewModel.setSpeed(speed = it)
                                    }
                                )
                            }
                            else -> {}
                        }
                    }
                )
            }
        }
    }.collectOnStarted(this)

    private fun observeUIAction() {
        viewModel.uiAction.onEach { uiAction ->
            when(uiAction) {
                is WorkoutCardViewModel.UIAction.OnFailure -> {
                    showBottomSheet(
                        title = getString(R.string.common_error_title),
                        subTitle = getString(R.string.common_error_subtitle),
                        btnTitle = getString(R.string.common_error_btn),
                        btnClick = { viewModel.initScreen() },
                        cancelable = false
                    )
                }
            }
        }.collectOnStarted(this)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_PLAYER_PLAY_WHEN_READY, viewModel.uiState.value.playerState?.isPlaying ?: false)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            viewModel.onRestore(
                savedInstanceState.getBoolean(KEY_PLAYER_PLAY_WHEN_READY)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onPause()
    }
}
