package com.vgve.workouts.presentation.workouts

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vgve.workouts.R
import com.vgve.workouts.databinding.FragmentWorkoutsBinding
import com.vgve.workouts.presentation.workouts.adapter.WorkoutsAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import androidx.navigation.fragment.findNavController
import com.vgve.core.utils.extensions.navigateSafe
import com.vgve.workouts.domain.models.WorkoutType
import com.vgve.workouts.presentation.utils.extensions.collectOnStarted
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class WorkoutsFragment: Fragment(R.layout.fragment_workouts) {

    private val binding: FragmentWorkoutsBinding by viewBinding(FragmentWorkoutsBinding::bind)
    private val viewModel: WorkoutsViewModel by viewModels()

    private val workoutsAdapter by lazy {
        WorkoutsAdapter(
            onClick = {
                viewModel.onClickWorkout(it.id)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUIState()
        observeUIAction()
    }

    private fun observeUIState() {
        viewModel.uiState.onEach { uiState ->
            with(binding) {
                // RecyclerView
                rvWorkouts.adapter = workoutsAdapter
                workoutsAdapter.items = uiState.workouts

                // Search
                sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = true
                    override fun onQueryTextChange(query: String?): Boolean {
                        viewModel.onSearch(query?.trim())
                        return true
                    }
                })

                // Chips
                cgTypes.setOnCheckedStateChangeListener { group, _ ->
                    val checkedId = group.checkedChipId
                    val type = when (checkedId) {
                        chipWorkout.id -> WorkoutType.Workout
                        chipLive.id -> WorkoutType.Live
                        chipComplex.id -> WorkoutType.Complex
                        chipAll.id -> null
                        else -> null
                    }
                    viewModel.onUpdateType(type)
                }

                // Empty layout
                llEmpty.apply {
                    llEmpty.isVisible = uiState.workouts.isEmpty() && !uiState.isLoading
                }
            }
        }.collectOnStarted(this)
    }

    private fun observeUIAction() {
        viewModel.uiAction.onEach { uiAction ->
            when (uiAction) {
                is WorkoutsViewModel.UIAction.OnFailure -> { }
                is WorkoutsViewModel.UIAction.OnClickWorkout -> {
                    findNavController().navigateSafe(
                        R.id.action_workoutsFragment_to_workoutCardFragment,
                        bundleOf()
                    )
                }
            }
        }.collectOnStarted(this)
    }
}
