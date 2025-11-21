package com.example.apppro.domain.usecase

import com.example.apppro.domain.model.Habit
import com.example.apppro.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

/**
 * Expoente del flujo de hábitos para la capa de presentación.
 */
class ObserveHabitsUseCase(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> = repository.observeHabits()
}
