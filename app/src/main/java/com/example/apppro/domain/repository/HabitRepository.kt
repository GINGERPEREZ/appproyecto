package com.example.apppro.domain.repository

import com.example.apppro.domain.model.Habit
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de persistencia para los hábitos. Permite observar cambios y modificarlos.
 */
interface HabitRepository {
    /**
     * Flujo que expone los hábitos almacenados en todo momento.
     */
    fun observeHabits(): Flow<List<Habit>>

    /**
     * Inserta un nuevo hábito (calculando ID automáticamente si es necesario).
     */
    suspend fun insertHabit(habit: Habit)

    /**
     * Sustituye un hábito existente por uno actualizado.
     */
    suspend fun updateHabit(habit: Habit)

    /**
     * Alterna una fecha de completado para un hábito dado.
     */
    suspend fun toggleCompletion(habitId: Long, date: LocalDate)

    /**
     * Ajusta el progreso relativo del hábito como si fuera un control de volumen.
     */
    suspend fun setHabitProgress(habitId: Long, progressFraction: Float, today: LocalDate = LocalDate.now())
}
