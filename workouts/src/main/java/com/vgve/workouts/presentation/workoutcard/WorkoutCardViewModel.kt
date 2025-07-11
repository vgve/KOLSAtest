package com.vgve.workouts.presentation.workoutcard

import androidx.lifecycle.ViewModel
import com.vgve.workouts.domain.usecases.GetWorkoutVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class WorkoutCardViewModel @Inject constructor(
    private val getWorkoutVideoUseCase: GetWorkoutVideoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _uiAction: Channel<UIAction> = Channel()
    val uiAction = _uiAction.receiveAsFlow()

    init {

    }

    data class UIState(
        val isLoading: Boolean = false,
    )

    sealed class UIAction {

    }
}
