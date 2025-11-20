package com.example.apppro.domain.usecase

import com.example.apppro.domain.model.Habit
import com.example.apppro.domain.repository.HabitRepository

class AddHabitUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        repository.insertHabit(habit)
    }
}
