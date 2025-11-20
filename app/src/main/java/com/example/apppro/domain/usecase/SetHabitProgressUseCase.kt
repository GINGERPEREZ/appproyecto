package com.example.apppro.domain.usecase

import com.example.apppro.domain.repository.HabitRepository
import java.time.LocalDate

class SetHabitProgressUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long, progressFraction: Float, today: LocalDate = LocalDate.now()) {
        repository.setHabitProgress(habitId, progressFraction, today)
    }
}