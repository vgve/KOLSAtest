package com.vgve.workouts.domain.usecases

import com.vgve.workouts.domain.repository.WorkoutsRepository
import javax.inject.Inject

class GetWorkoutVideoUseCase @Inject constructor(
    private val repository: WorkoutsRepository
) {
    suspend operator fun invoke(id: Int) =
        repository.getWorkoutVideo(id)
}
