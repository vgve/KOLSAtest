package com.vgve.workouts.data.models

import com.google.gson.annotations.SerializedName
import com.vgve.workouts.domain.models.WorkoutModel
import com.vgve.workouts.domain.models.WorkoutType

data class WorkoutModelEntity(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("type")
    val type: WorkoutTypeEntity?,
    @SerializedName("duration")
    val duration: String?
)

enum class WorkoutTypeEntity{
    Workout, Live, Complex
}

internal fun WorkoutTypeEntity.toDomain() = when(this) {
    WorkoutTypeEntity.Workout -> WorkoutType.Workout
    WorkoutTypeEntity.Live -> WorkoutType.Live
    WorkoutTypeEntity.Complex -> WorkoutType.Complex
}

internal fun WorkoutModelEntity.toDomain() = WorkoutModel(
    id = id ?: -1,
    title = title.orEmpty(),
    description = description.orEmpty(),
    type = type?.toDomain(),
    duration = duration.orEmpty()
)
