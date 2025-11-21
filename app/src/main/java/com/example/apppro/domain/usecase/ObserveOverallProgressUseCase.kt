package com.example.apppro.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Calcula el promedio general del progreso de todos los hábitos para mostrar en la UI.
 */
class ObserveOverallProgressUseCase(
    private val observeHabitProgressUseCase: ObserveHabitProgressUseCase
) {
    operator fun invoke(): Flow<Float> {
        return observeHabitProgressUseCase().map { progressList ->
            if (progressList.isEmpty()) return@map 0f
            progressList.map { it.progressFraction }.average().toFloat()
        }
    }
}
