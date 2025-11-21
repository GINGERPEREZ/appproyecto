package com.example.apppro.domain.usecase

import com.example.apppro.domain.repository.HabitRepository
import java.time.LocalDate

/**
 * Permite marcar una fecha como completada o deshacerla para un hábito concreto.
 */
class ToggleHabitCompletionUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate = LocalDate.now()) {
        repository.toggleCompletion(habitId, date)
    }
}
