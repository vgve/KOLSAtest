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

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    private val _uiAction: Channel<UIAction> = Channel()
    val uiAction = _uiAction.receiveAsFlow()

    private var workouts = emptyList<WorkoutModel>()

    init {
        initScreen()
    }
    
    fun initScreen() {
        launchSafe(
            errorHandler = { exception ->
                Log.e(TAG, "$exception")
                viewModelScope.launch { _uiAction.send(UIAction.OnFailure) }
            },
            block = {
                val result = getWorkoutsUseCase.invoke()
                workouts = result
                _uiState.update { it.copy(workouts = result) }
            }
        ).track { _uiState.update { uiState -> uiState.copy(isLoading = it) } }
    }

    fun onClickWorkout(workout: WorkoutModel) {
        viewModelScope.launch {
            _uiAction.send(
                UIAction.OnClickWorkout(workout)
            )
        }
    }

    fun onUpdateType(type: WorkoutType?) {
        _uiState.update { it.copy(type = type) }
        onUpdateWorkouts()
    }

    fun onSearch(query: String?) {
        _uiState.update { it.copy(query = query) }
        onUpdateWorkouts()
    }

    private fun onUpdateWorkouts() {
        _uiState.update { uiState ->
            uiState.copy(
                workouts = workouts.filter {
                    it.type == (uiState.type ?: it.type) && it.title.contains(
                        uiState.query ?: "",
                        ignoreCase = true
                    )
                }
            )
        }
    }

    data class UIState(
        val isLoading: Boolean = false,
        val workouts: List<WorkoutModel> = emptyList(),
        val type: WorkoutType? = null,
        val query: String? = null
    )

    sealed class UIAction {
        data class OnClickWorkout(val workout: WorkoutModel): UIAction()
        data object OnFailure : UIAction()
    }
}
