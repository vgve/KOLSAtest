package com.vgve.workouts.data.repository

import com.vgve.core.utils.extensions.toResult
import com.vgve.workouts.data.models.toDomain
import com.vgve.workouts.data.service.WorkoutsService
import com.vgve.workouts.domain.models.WorkoutModel
import com.vgve.workouts.domain.models.VideoWorkoutModel
import com.vgve.workouts.domain.repository.WorkoutsRepository

class WorkoutsRepositoryImpl(
    private val service: WorkoutsService
): WorkoutsRepository {

    override suspend fun getWorkouts(): List<WorkoutModel> {
        return service.getWorkouts()
            .toResult()
            .fold(
                onSuccess = { response -> response.map { it.toDomain() } },
                onFailure = { _ -> emptyList() }
            )
    }

    override suspend fun getWorkoutVideo(id: Int): VideoWorkoutModel {
        return service.getWorkoutVideo(id)
            .toResult()
            .fold(
                onSuccess = { response -> response.toDomain() },
                onFailure = { error ->
                    throw error
                }
            )
    }
}
