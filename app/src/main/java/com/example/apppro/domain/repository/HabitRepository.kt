package com.example.apppro.domain.repository

import com.example.apppro.domain.model.Habit
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabits(): Flow<List<Habit>>
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun toggleCompletion(habitId: Long, date: LocalDate)
    suspend fun setHabitProgress(habitId: Long, progressFraction: Float, today: LocalDate = LocalDate.now())
}
