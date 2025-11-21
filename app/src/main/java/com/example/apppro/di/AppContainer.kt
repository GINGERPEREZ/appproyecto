package com.example.apppro.di

import android.content.Context
import com.example.apppro.data.FocusReminderRepository
import com.example.apppro.data.repository.HabitDataStoreRepository
import com.example.apppro.domain.usecase.AddHabitUseCase
import com.example.apppro.domain.usecase.ObserveHabitProgressUseCase
import com.example.apppro.domain.usecase.ObserveHabitsUseCase
import com.example.apppro.domain.usecase.ObserveOverallProgressUseCase
import com.example.apppro.domain.usecase.SetHabitProgressUseCase
import com.example.apppro.domain.usecase.ToggleHabitCompletionUseCase
import com.example.apppro.ui.viewmodel.HabitViewModelFactory

/**
 * Contenedor que crea las dependencias compartidas (repositorio, casos de uso y fábrica de ViewModel).
 */
class AppContainer(context: Context) {
    private val repository: HabitDataStoreRepository by lazy { HabitDataStoreRepository(context) }

    private val focusReminderRepository: FocusReminderRepository by lazy { FocusReminderRepository(context) }
    private val observeHabitsUseCase: ObserveHabitsUseCase by lazy { ObserveHabitsUseCase(repository) }
    private val observeHabitProgressUseCase: ObserveHabitProgressUseCase by lazy { ObserveHabitProgressUseCase(observeHabitsUseCase) }
    private val observeOverallProgressUseCase: ObserveOverallProgressUseCase by lazy { ObserveOverallProgressUseCase(observeHabitProgressUseCase) }
    private val addHabitUseCase: AddHabitUseCase by lazy { AddHabitUseCase(repository) }
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase by lazy { ToggleHabitCompletionUseCase(repository) }
    private val setHabitProgressUseCase: SetHabitProgressUseCase by lazy { SetHabitProgressUseCase(repository) }

    val habitViewModelFactory: HabitViewModelFactory by lazy {
        // Fábrica que inyecta los casos de uso necesarios al ViewModel.
        HabitViewModelFactory(
            observeHabitsUseCase,
            observeHabitProgressUseCase,
            observeOverallProgressUseCase,
            addHabitUseCase,
            toggleHabitCompletionUseCase,
            setHabitProgressUseCase,
            focusReminderRepository
        )
    }
}
