package com.vgve.workouts.domain.models

data class WorkoutModel(
    val id: Int?,
    val title: String?,
    val description: String?,
    val type: WorkoutType?,
    val duration: String?
)

enum class WorkoutType{
    Workout, Live, Complex
}
