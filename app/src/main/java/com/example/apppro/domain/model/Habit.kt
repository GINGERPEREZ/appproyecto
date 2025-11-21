package com.example.apppro.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Representa los estados posibles del progreso de un hábito dentro de la ventana actual.
 */
enum class HabitStatus {
    COMPLETED,
    IN_PROGRESS,
    PENDING
}

data class Habit(
    val id: Long,
    val name: String,
    val createdAt: LocalDate = LocalDate.now(),
    val completions: List<LocalDate> = emptyList(),
    val windowDays: Int = 7
)

data class HabitProgress(
    val habitId: Long,
    val progressFraction: Float,
    val status: HabitStatus
)

/**
 * Calcula el progreso (fracción y estado) del hábito según su ventana de seguimiento.
 */
fun Habit.progressOn(today: LocalDate = LocalDate.now()): HabitProgress {
    if (windowDays <= 0) {
        return HabitProgress(habitId = id, progressFraction = 0f, status = HabitStatus.PENDING)
    }

    val windowStart = today.minus(windowDays.toLong() - 1, ChronoUnit.DAYS)
    val completedInWindow = completions.count { date ->
        !date.isBefore(windowStart) && !date.isAfter(today)
    }
    val fraction = (completedInWindow.toFloat() / windowDays.toFloat()).coerceIn(0f, 1f)
    val status = when {
        fraction >= 1f -> HabitStatus.COMPLETED
        fraction > 0f -> HabitStatus.IN_PROGRESS
        else -> HabitStatus.PENDING
    }
    return HabitProgress(habitId = id, progressFraction = fraction, status = status)
}
