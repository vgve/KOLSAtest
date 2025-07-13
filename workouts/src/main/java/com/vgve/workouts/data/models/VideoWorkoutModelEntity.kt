package com.vgve.workouts.data.models

import com.google.gson.annotations.SerializedName
import com.vgve.workouts.domain.models.VideoWorkoutModel

data class VideoWorkoutModelEntity(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("link")
    val link: String?
)

internal fun VideoWorkoutModelEntity.toDomain() = VideoWorkoutModel(
    id = id ?: -1,
    duration = duration.orEmpty(),
    link = link.orEmpty()
)
