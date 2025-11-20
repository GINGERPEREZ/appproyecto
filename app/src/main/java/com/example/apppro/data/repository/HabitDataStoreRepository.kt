package com.example.apppro.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.apppro.data.deserializeHabits
import com.example.apppro.data.serializeHabits
import com.example.apppro.data.HABITS_KEY
import com.example.apppro.data.dataStore
import com.example.apppro.domain.model.Habit
import com.example.apppro.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HabitDataStoreRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HabitRepository {

    override fun observeHabits(): Flow<List<Habit>> {
        return context.dataStore.data
            .map { prefs -> prefs[HABITS_KEY] ?: "" }
            .map { serialized -> deserializeHabits(serialized) }
    }

    override suspend fun insertHabit(habit: Habit) = withContext(ioDispatcher) {
        val current = currentHabits()
        val nextId = (current.maxOfOrNull { it.id } ?: 0L) + 1
        val toSave = current + habit.copy(id = nextId)
        saveHabits(toSave)
    }

    override suspend fun updateHabit(habit: Habit) = withContext(ioDispatcher) {
        val updated = currentHabits().map { existing -> if (existing.id == habit.id) habit else existing }
        saveHabits(updated)
    }

    override suspend fun toggleCompletion(habitId: Long, date: LocalDate) = withContext(ioDispatcher) {
        val updated = currentHabits().map { habit ->
            if (habit.id != habitId) return@map habit
            val newCompletions = if (habit.completions.contains(date)) {
                habit.completions - date
            } else {
                habit.completions + date
            }
            habit.copy(completions = newCompletions)
        }
        saveHabits(updated)
    }

    override suspend fun setHabitProgress(habitId: Long, progressFraction: Float, today: LocalDate) = withContext(ioDispatcher) {
        val clamped = progressFraction.coerceIn(0f, 1f)
        val updated = currentHabits().map { habit ->
            if (habit.id != habitId) return@map habit
            val windowDays = habit.windowDays.coerceAtLeast(1)
            val filledDays = (clamped * windowDays).toInt().coerceIn(0, windowDays)
            val windowDates = (0 until windowDays).map { idx -> today.minus(idx.toLong(), ChronoUnit.DAYS) }
            val cappedCompletions = windowDates.take(filledDays).toSet()
            val windowStart = today.minus((windowDays - 1).toLong(), ChronoUnit.DAYS)
            val carriedOver = habit.completions.filter { it.isBefore(windowStart) }
            val result = (carriedOver + cappedCompletions).distinct()
            habit.copy(completions = result)
        }
        saveHabits(updated)
    }

    private suspend fun currentHabits(): List<Habit> {
        val prefs = context.dataStore.data.first()
        val serialized = prefs[HABITS_KEY] ?: ""
        return deserializeHabits(serialized)
    }

    private suspend fun saveHabits(habits: List<Habit>) {
        context.dataStore.edit { prefs ->
            if (habits.isEmpty()) {
                prefs.remove(HABITS_KEY)
            } else {
                prefs[HABITS_KEY] = serializeHabits(habits)
            }
        }
    }
}
