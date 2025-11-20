package com.example.apppro.domain.usecase

import com.example.apppro.domain.model.HabitProgress
import com.example.apppro.domain.model.progressOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveHabitProgressUseCase(
    private val observeHabitsUseCase: ObserveHabitsUseCase
) {
    operator fun invoke(): Flow<List<HabitProgress>> {
        return observeHabitsUseCase().map { list ->
            list.map { habit -> habit.progressOn() }
        }
    }
}
