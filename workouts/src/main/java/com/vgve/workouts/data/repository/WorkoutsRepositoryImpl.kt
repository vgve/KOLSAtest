package com.vgve.workouts.data.repository

import com.vgve.core.utils.dataOrThrow
import com.vgve.workouts.data.models.toDomain
import com.vgve.workouts.data.service.WorkoutsService
import com.vgve.workouts.domain.models.WorkoutModel
import com.vgve.workouts.domain.models.WorkoutVideoModel
import com.vgve.workouts.domain.repository.WorkoutsRepository

class WorkoutsRepositoryImpl(
    private val service: WorkoutsService
): WorkoutsRepository {

    override suspend fun getWorkouts(): List<WorkoutModel> =
        service.getWorkouts()
            .dataOrThrow()
            .map {
                it.toDomain()
            }

    override suspend fun getWorkoutVideo(id: Int): WorkoutVideoModel =
        service.getWorkoutVideo(id)
            .dataOrThrow()
            .toDomain()
}
