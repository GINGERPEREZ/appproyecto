package com.example.apppro.testutil

import com.example.apppro.domain.model.Habit
import com.example.apppro.domain.repository.HabitRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class SetProgressCall(val habitId: Long, val fraction: Float, val date: LocalDate)

class RecordingHabitRepository(initialHabits: List<Habit> = emptyList()) : HabitRepository {
    private val habits = MutableStateFlow(initialHabits)
    val setProgressCalls = mutableListOf<SetProgressCall>()

    override fun observeHabits(): Flow<List<Habit>> = habits
    override suspend fun insertHabit(habit: Habit) {
        habits.value = habits.value + habit
    }

    override suspend fun updateHabit(habit: Habit) {
        habits.value = habits.value.map { if (it.id == habit.id) habit else it }
    }

    override suspend fun toggleCompletion(habitId: Long, date: LocalDate) {
        habits.value = habits.value.map { habit ->
            if (habit.id != habitId) return@map habit
            val newCompletions = if (habit.completions.contains(date)) habit.completions - date else habit.completions + date
            habit.copy(completions = newCompletions)
        }
    }

    override suspend fun setHabitProgress(habitId: Long, progressFraction: Float, today: LocalDate) {
        setProgressCalls += SetProgressCall(habitId, progressFraction, today)
    }
}