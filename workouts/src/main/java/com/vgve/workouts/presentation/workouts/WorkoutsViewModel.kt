package com.vgve.workouts.presentation.workouts

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vgve.workouts.domain.models.WorkoutModel
import com.vgve.workouts.domain.models.WorkoutType
import com.vgve.workouts.domain.usecases.GetWorkoutsUseCase
import com.vgve.workouts.presentation.utils.extensions.launchSafe
import com.vgve.workouts.presentation.utils.extensions.track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val getWorkoutsUseCase: GetWorkoutsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutsState())
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<WorkoutsEffect> = Channel()
    val effect: Flow<WorkoutsEffect> = _effect.receiveAsFlow()

    private var workouts = emptyList<WorkoutModel>()

    init {
        handleIntent(WorkoutsIntent.InitialLoad)
    }

    fun handleIntent(intent: WorkoutsIntent) {
        when (intent) {
            is WorkoutsIntent.InitialLoad -> loadWorkouts()
            is WorkoutsIntent.WorkoutClicked -> onWorkoutClicked(intent.workout)
            is WorkoutsIntent.TypeFilterChanged -> onTypeFilterChanged(intent.type)
            is WorkoutsIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
        }
    }

    private fun loadWorkouts() {
        launchSafe(
            errorHandler = { exception ->
                Log.e(TAG, "Error loading workouts: $exception")
                viewModelScope.launch { _effect.send(WorkoutsEffect.LoadFailed) }
            },
            block = {
                workouts = getWorkoutsUseCase.invoke()
                updateFilteredWorkouts()
            }
        ).track { isLoading ->
            _uiState.update { it.copy(isLoading = isLoading) }
        }
    }

    private fun updateFilteredWorkouts() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(
                workouts = workouts.filter { workout ->
                    (currentState.selectedType == null || workout.type == currentState.selectedType) &&
                            (currentState.searchQuery.isNullOrBlank() ||
                                    workout.title.contains(currentState.searchQuery, ignoreCase = true))
                }
            )
        }
    }

    private fun onWorkoutClicked(workout: WorkoutModel) {
        viewModelScope.launch {
            _effect.send(WorkoutsEffect.NavigateToWorkout(workout))
        }
    }

    private fun onTypeFilterChanged(type: WorkoutType?) {
        _uiState.update { it.copy(selectedType = type) }
        updateFilteredWorkouts()
    }

    private fun onSearchQueryChanged(query: String?) {
        _uiState.update { it.copy(searchQuery = query) }
        updateFilteredWorkouts()
    }

    data class WorkoutsState(
        val isLoading: Boolean = false,
        val workouts: List<WorkoutModel> = emptyList(),
        val selectedType: WorkoutType? = null,
        val searchQuery: String? = null
    )

    sealed class WorkoutsIntent {
        data object InitialLoad : WorkoutsIntent()
        data class WorkoutClicked(val workout: WorkoutModel) : WorkoutsIntent()
        data class TypeFilterChanged(val type: WorkoutType?) : WorkoutsIntent()
        data class SearchQueryChanged(val query: String?) : WorkoutsIntent()
    }

    sealed class WorkoutsEffect {
        data class NavigateToWorkout(val workout: WorkoutModel) : WorkoutsEffect()
        data object LoadFailed : WorkoutsEffect()
    }
}
