package com.vgve.workouts.domain.repository

import com.vgve.workouts.domain.models.WorkoutModel
import com.vgve.workouts.domain.models.WorkoutVideoModel

interface WorkoutsRepository {

    suspend fun getWorkouts(): List<WorkoutModel>

    suspend fun getWorkoutVideo(id: Int): WorkoutVideoModel
}
