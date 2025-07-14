package com.vgve.workouts.data.models

import com.google.gson.annotations.SerializedName
import com.vgve.core.BuildConfig
import com.vgve.workouts.domain.models.VideoWorkoutModel
import java.net.URI

data class VideoWorkoutModelEntity(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("link")
    val link: String?
)

internal fun VideoWorkoutModelEntity.toDomain() = VideoWorkoutModel(
    id = id ?: throw IllegalArgumentException("Id field is null"),
    duration = duration?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("Duration field is null"),
    link = URI
        .create(BuildConfig.BASE_URL)
        .resolve(link?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("Link field is null"))
        .toString()
)
