package com.vgve.workouts.presentation.workouts

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.annotation.OptIn
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import com.vgve.workouts.R
import com.vgve.workouts.databinding.FragmentWorkoutsBinding
import com.vgve.workouts.presentation.workouts.adapter.WorkoutsAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import androidx.navigation.fragment.findNavController
import com.vgve.workouts.presentation.utils.extensions.navigateSafe
import com.vgve.workouts.domain.models.WorkoutType
import com.vgve.workouts.presentation.utils.extensions.collectOnStarted
import com.vgve.workouts.presentation.utils.extensions.showBottomSheet
import com.vgve.workouts.presentation.workoutcard.WorkoutCardViewModel
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class WorkoutsFragment: Fragment(R.layout.fragment_workouts) {

    private val binding: FragmentWorkoutsBinding by viewBinding(FragmentWorkoutsBinding::bind)
    private val viewModel: WorkoutsViewModel by viewModels()

    private val workoutsAdapter by lazy {
        WorkoutsAdapter(
            onClick = {
                viewModel.handleIntent(WorkoutsViewModel.WorkoutsIntent.WorkoutClicked(it))
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeState()
        observeEffects()
    }

    private fun setupViews() {
        with(binding) {
            // RecyclerView setup
            rvWorkouts.adapter = workoutsAdapter

            // Search setup
            sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = true
                override fun onQueryTextChange(query: String?): Boolean {
                    viewModel.handleIntent(
                        WorkoutsViewModel.WorkoutsIntent.SearchQueryChanged(query?.trim())
                    )
                    return true
                }
            })

            // Chips setup
            cgTypes.setOnCheckedStateChangeListener { group, _ ->
                val checkedId = group.checkedChipId
                val type = when (checkedId) {
                    chipWorkout.id -> WorkoutType.Workout
                    chipLive.id -> WorkoutType.Live
                    chipComplex.id -> WorkoutType.Complex
                    chipAll.id -> null
                    else -> null
                }
                viewModel.handleIntent(
                    WorkoutsViewModel.WorkoutsIntent.TypeFilterChanged(type)
                )
            }
        }
    }

    private fun observeState() {
        viewModel.uiState.onEach { state ->
            with(binding) {
                // Update workouts list
                workoutsAdapter.items = state.workouts

                // Update empty state visibility
                llEmpty.apply {
                    llEmpty.isVisible = state.workouts.isEmpty() && !state.isLoading
                }

                // Update loading state
                clMain.isVisible = !state.isLoading
                pbWorkouts.isVisible = state.isLoading
            }
        }.collectOnStarted(this)
    }

    @OptIn(UnstableApi::class)
    private fun observeEffects() {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is WorkoutsViewModel.WorkoutsEffect.NavigateToWorkout -> {
                    findNavController().navigateSafe(
                        R.id.action_workoutsFragment_to_workoutCardFragment,
                        bundleOf(
                            WorkoutCardViewModel.KEY_ARGS_WORKOUT to effect.workout
                        )
                    )
                }
                WorkoutsViewModel.WorkoutsEffect.LoadFailed -> {
                    showBottomSheet(
                        title = getString(R.string.common_error_title),
                        subTitle = getString(R.string.common_error_subtitle),
                        btnTitle = getString(R.string.common_error_btn),
                        btnClick = { viewModel.handleIntent(WorkoutsViewModel.WorkoutsIntent.InitialLoad) },
                        cancelable = false
                    )
                }
            }
        }.collectOnStarted(this)
    }
}
