package com.example.apppro.domain.usecase

import com.example.apppro.domain.repository.HabitRepository
import java.time.LocalDate

/**
 * Expone una interfaz para ajustar el progreso como si fuera un control de volumen.
 */
class SetHabitProgressUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long, progressFraction: Float, today: LocalDate = LocalDate.now()) {
        repository.setHabitProgress(habitId, progressFraction, today)
    }
}