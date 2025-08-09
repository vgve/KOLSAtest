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

        setupViews()
        observeState()
        observeEffects()
    }

    private fun setupViews() {
        with(binding) {
            // Toolbar setup
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            // Player controls setup
            // Play
            pvWorkout.play.setOnClickListener {
                viewModel.handleIntent(
                    WorkoutCardViewModel.WorkoutCardIntent.PlayerAction(
                        WorkoutCardViewModel.PlayerAction.Play
                    )
                )
            }

            // Replay
            pvWorkout.replay.setOnClickListener {
                viewModel.handleIntent(
                    WorkoutCardViewModel.WorkoutCardIntent.PlayerAction(
                        WorkoutCardViewModel.PlayerAction.Replay
                    )
                )
            }

            // Mute
            pvWorkout.mute.setOnClickListener {
                viewModel.handleIntent(
                    WorkoutCardViewModel.WorkoutCardIntent.PlayerAction(
                        WorkoutCardViewModel.PlayerAction.Mute
                    )
                )
            }

            // Forward
            pvWorkout.forward.setOnClickListener {
                viewModel.handleIntent(
                    WorkoutCardViewModel.WorkoutCardIntent.PlayerAction(
                        WorkoutCardViewModel.PlayerAction.Forward
                    )
                )
            }

            // Rewind
            pvWorkout.rewind.setOnClickListener {
                viewModel.handleIntent(
                    WorkoutCardViewModel.WorkoutCardIntent.PlayerAction(
                        WorkoutCardViewModel.PlayerAction.Rewind
                    )
                )
            }

            // Settings
            pvWorkout.settings.setOnClickListener {
                showSettingsMenu()
            }
        }
    }

    private fun observeState() {
        viewModel.uiState.onEach { state ->
            with(binding) {
                // Update toolbar
                toolbar.tvToolbarTitle.text = state.workout?.title

                // Update workout info
                tvDescription.apply {
                    isVisible = !state.workout?.description.isNullOrEmpty()
                    text = state.workout?.description
                }
                tvType.text = state.workout?.type?.toResString()?.let { getString(it) }
                tvDuration.apply {
                    isVisible = state.videoWorkout?.duration?.isInteger() ?: false
                    text = getString(R.string.duration_minutes, state.videoWorkout?.duration)
                }

                // Update loading state
                clMain.isVisible = !state.isLoading
                pbWorkoutCard.isVisible = state.isLoading

                // Update player
                pvWorkout.apply {
                    isVisible = !state.isLoading
                    state.player?.let { player ->
                        setPlayer(player)
                    }
                }

                // Update player controls
                with(binding.pvWorkout) {
                    setPlayResource(state.playerState?.isPlaying == true)
                    play.isVisible = state.playerState?.isEnded == false
                    replay.isVisible = state.playerState?.isEnded == true
                    mute.isEnabled = state.playerState?.isEnded == false
                    setMuteResource(state.playerState?.isMute == true)
                    forward.isVisible = state.playerState?.isEnded == false
                    rewind.isVisible = state.playerState?.isEnded == false
                }
            }
        }.collectOnStarted(this)
    }

    private fun observeEffects() {
        viewModel.effect.onEach { effect ->
            when(effect) {
                WorkoutCardViewModel.WorkoutCardEffect.LoadFailed -> {
                    showBottomSheet(
                        title = getString(R.string.common_error_title),
                        subTitle = getString(R.string.common_error_subtitle),
                        btnTitle = getString(R.string.common_error_btn),
                        btnClick = {
                            viewModel.handleIntent(WorkoutCardViewModel.WorkoutCardIntent.InitialLoad)
                        },
                        cancelable = false
                    )
                }
            }
        }.collectOnStarted(this)
    }

    private fun showSettingsMenu() {
        val currentState = viewModel.uiState.value

        showSettingsPopup(
            view = binding.pvWorkout.settings,
            onClick = { type ->
                when(type) {
                    SettingsType.QUALITY -> {
                        showQualityPopup(
                            view = binding.pvWorkout.settings,
                            resolutions = currentState.playerState?.availableQualities,
                            onClick = {
                                viewModel.handleIntent(
                                    WorkoutCardViewModel.WorkoutCardIntent.SetVideoQuality(it)
                                )
                            }
                        )
                    }
                    SettingsType.SPEED -> {
                        showSpeedPopup(
                            view = binding.pvWorkout.settings,
                            onClick = {
                                viewModel.handleIntent(
                                    WorkoutCardViewModel.WorkoutCardIntent.SetPlaybackSpeed(it)
                                )
                            }
                        )
                    }
                    else -> {}
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        viewModel.handleIntent(
            WorkoutCardViewModel.WorkoutCardIntent.PlayerAction(
                WorkoutCardViewModel.PlayerAction.Pause
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_PLAYER_PLAY_WHEN_READY, viewModel.uiState.value.playerState?.isPlaying ?: false)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            viewModel.handleIntent(
                WorkoutCardViewModel.WorkoutCardIntent.RestorePlayer(
                    savedInstanceState.getBoolean(KEY_PLAYER_PLAY_WHEN_READY)
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.handleIntent(
            WorkoutCardViewModel.WorkoutCardIntent.PlayerAction(
                WorkoutCardViewModel.PlayerAction.Pause
            )
        )
    }
}
