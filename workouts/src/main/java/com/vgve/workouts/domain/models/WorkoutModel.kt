package com.vgve.workouts.domain.models

import android.os.Parcelable
import com.vgve.workouts.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutModel(
    val id: Int,
    val title: String,
    val description: String,
    val type: WorkoutType,
    val duration: String
) : Parcelable

enum class WorkoutType{
    Workout, Live, Complex, Unknown
}

internal fun WorkoutType.toResString() = when(this) {
    WorkoutType.Workout -> R.string.type_workout
    WorkoutType.Live -> R.string.type_complex
    WorkoutType.Complex -> R.string.type_complex
    WorkoutType.Unknown -> R.string.type_unknown
}
