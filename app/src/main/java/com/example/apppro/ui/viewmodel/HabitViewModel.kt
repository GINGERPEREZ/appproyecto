package com.example.apppro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Habit(
    val id: Long,
    val name: String,
    val createdAt: LocalDate = LocalDate.now(),
    var completions: MutableList<LocalDate> = mutableListOf(),
    val goalDays: Int = 7 // number of days for the target window (week by default)
)

class HabitViewModel : ViewModel() {
    private val _habits = mutableStateListOf<Habit>()
    val habits: List<Habit> get() = _habits

    private var nextId by mutableStateOf(1L)
    // Use StateFlow for ViewModel-level observable state (independent from Compose runtime)
    private val _overallProgress = MutableStateFlow(0f)
    val overallProgress: StateFlow<Float> get() = _overallProgress

    init {
        // Seed with a couple examples
        addHabit("Tomar agua")
        addHabit("Estudiar 30 min")
    }

    // Compute progress for a specific habit as fraction of completions in the recent window
    fun habitProgress(habitId: Long): Float {
        val h = _habits.find { it.id == habitId } ?: return 0f
        if (h.goalDays <= 0) return 0f
        val today = LocalDate.now()
        val windowStart = today.minus(h.goalDays.toLong() - 1, ChronoUnit.DAYS)
        val count = h.completions.count { d -> !d.isBefore(windowStart) && !d.isAfter(today) }
        return (count.toFloat() / h.goalDays.toFloat()).coerceAtMost(1f)
    }

    private fun recomputeOverallProgress() {
        val value = if (_habits.isEmpty()) 0f
        else {
            val completedToday = _habits.count { it.completions.contains(LocalDate.now()) }
            completedToday.toFloat() / _habits.size.toFloat()
        }
        _overallProgress.value = value
    }

    fun addHabit(name: String, goalDays: Int = 7) {
        _habits.add(Habit(id = nextId++, name = name, goalDays = goalDays))
        // Recompute overall progress immediately after adding a new habit so UI updates.
        recomputeOverallProgress()
    }

    fun toggleCompletion(habitId: Long, date: LocalDate = LocalDate.now()) {
        val h = _habits.find { it.id == habitId } ?: return
        if (h.completions.contains(date)) h.completions.remove(date) else h.completions.add(date)
        // Trigger recomposition by replacing the object in the list
        val index = _habits.indexOfFirst { it.id == habitId }
        if (index >= 0) {
            _habits[index] = h.copy(completions = h.completions.toMutableList())
            // Recompute overall progress when a completion toggles
            recomputeOverallProgress()
        }
    }

    fun completionsCount(habitId: Long): Int {
        return _habits.find { it.id == habitId }?.completions?.size ?: 0
    }

    // Replace the internal list with deserialized data (used by persistence loader)
    fun setHabits(newHabits: List<Habit>) {
        _habits.clear()
        _habits.addAll(newHabits.map { it.copy(completions = it.completions.toMutableList()) })
        val maxId = _habits.maxOfOrNull { it.id } ?: 0L
        nextId = maxId + 1
        recomputeOverallProgress()
    }

    // UI selection helper: highlight or navigate to a habit
    var selectedHabitId by mutableStateOf<Long?>(null)

    fun selectHabit(id: Long?) {
        selectedHabitId = id
    }
}
