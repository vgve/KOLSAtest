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
    val type: Int?,
    @SerializedName("duration")
    val duration: String?
)

internal fun Int.typeToDomain() = when(this) {
    1 -> WorkoutType.Workout
    2 -> WorkoutType.Live
    3 -> WorkoutType.Complex
    else -> WorkoutType.Unknown
}

internal fun WorkoutModelEntity.toDomain() = WorkoutModel(
    id = id ?: throw IllegalArgumentException("Id field is null"),
    title = title?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("Title field is null"),
    description = description.orEmpty(),
    type = type?.typeToDomain() ?: WorkoutType.Unknown,
    duration = duration?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("Duration field is null")
)
