package com.vgve.workouts.domain.usecases

import com.vgve.workouts.domain.repository.WorkoutsRepository
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(
    private val repository: WorkoutsRepository
) {
    suspend operator fun invoke() =
        repository.getWorkouts()
}
