package com.vgve.workouts.presentation.workoutcard

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vgve.workouts.R
import com.vgve.workouts.databinding.FragmentWorkoutCardBinding
import com.vgve.workouts.domain.models.toResString
import com.vgve.workouts.presentation.utils.extensions.collectOnStarted
import com.vgve.workouts.presentation.utils.extensions.isInteger
import com.vgve.workouts.presentation.utils.extensions.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class WorkoutCardFragment: Fragment(R.layout.fragment_workout_card) {

    companion object {
        const val KEY_PLAYER_POSITION = "current_position"
        const val KEY_PLAYER_PLAY_WHEN_READY = "play_when_ready"
    }

    private val binding: FragmentWorkoutCardBinding by viewBinding(FragmentWorkoutCardBinding::bind)
    private val viewModel: WorkoutCardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUIState()
        observeUIAction()
    }

    private fun observeUIState() {
        viewModel.uiState.onEach { uiState ->
            with(binding) {
                // Toolbar
                toolbar.apply {
                    ivBack.setOnClickListener {
                        findNavController().popBackStack()
                    }
                    tvToolbarTitle.text = uiState.workout?.title
                }

                // Info
                tvDescription.text = uiState.workout?.description
                tvType.text = uiState.workout?.type?.toResString()?.let { getString(it) }
                tvDuration.apply {
                    isVisible = uiState.videoWorkout?.duration?.isInteger() ?: false
                    text = getString(R.string.duration_minutes, uiState.videoWorkout?.duration)
                }

                // Progress bar
                clMain.isVisible = !uiState.isLoading
                pbWorkoutCard.isVisible = uiState.isLoading

                // Player
                uiState.player?.let {
                    pvWorkout.player = it
                }
            }
        }.collectOnStarted(this)
    }

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
        viewModel.onStopVideo()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(KEY_PLAYER_POSITION, viewModel.uiState.value.player?.currentPosition ?: 0L)
        outState.putBoolean(KEY_PLAYER_PLAY_WHEN_READY, viewModel.uiState.value.playerState?.isPlayWhenReady ?: false)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            viewModel.restoreSettings(
                savedInstanceState.getLong(KEY_PLAYER_POSITION),
                savedInstanceState.getBoolean(KEY_PLAYER_PLAY_WHEN_READY)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onStopVideo()
    }
}
