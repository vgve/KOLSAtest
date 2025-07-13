package com.vgve.workouts.data.service

import com.vgve.workouts.data.models.WorkoutModelEntity
import com.vgve.workouts.data.models.VideoWorkoutModelEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WorkoutsService {

    @GET("get_workouts")
    suspend fun getWorkouts(): Response<List<WorkoutModelEntity>>

    @GET("get_video")
    suspend fun getWorkoutVideo(
        @Query("id") id: Int
    ): Response<VideoWorkoutModelEntity>
}
