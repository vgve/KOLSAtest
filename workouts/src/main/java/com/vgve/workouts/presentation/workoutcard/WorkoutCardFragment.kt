package com.vgve.workouts.presentation.workoutcard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vgve.workouts.R
import com.vgve.workouts.databinding.FragmentWorkoutCardBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding

@AndroidEntryPoint
class WorkoutCardFragment: Fragment(R.layout.fragment_workout_card) {

    private val binding: FragmentWorkoutCardBinding by viewBinding(FragmentWorkoutCardBinding::bind)
    private val viewModel: WorkoutCardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUIState()
        observeUIAction()
    }

    private fun observeUIState() {

    }

    private fun observeUIAction() {

    }
}
