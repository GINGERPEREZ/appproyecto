package com.example.apppro.ui.viewmodel

import com.example.apppro.domain.usecase.AddHabitUseCase
import com.example.apppro.domain.usecase.ObserveHabitProgressUseCase
import com.example.apppro.domain.usecase.ObserveHabitsUseCase
import com.example.apppro.domain.usecase.ObserveOverallProgressUseCase
import com.example.apppro.domain.usecase.SetHabitProgressUseCase
import com.example.apppro.domain.usecase.ToggleHabitCompletionUseCase
import com.example.apppro.testutil.MainDispatcherRule
import com.example.apppro.testutil.RecordingHabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `setHabitProgress delegates to repository`() = runTest(mainDispatcherRule.dispatcher) {
        val repository = RecordingHabitRepository()
        val observeHabitsUseCase = ObserveHabitsUseCase(repository)
        val observeHabitProgressUseCase = ObserveHabitProgressUseCase(observeHabitsUseCase)
        val observeOverallProgressUseCase = ObserveOverallProgressUseCase(observeHabitProgressUseCase)
        val addHabitUseCase = AddHabitUseCase(repository)
        val toggleHabitCompletionUseCase = ToggleHabitCompletionUseCase(repository)
        val setHabitProgressUseCase = SetHabitProgressUseCase(repository)

        val viewModel = HabitViewModel(
            observeHabitsUseCase,
            observeHabitProgressUseCase,
            observeOverallProgressUseCase,
            addHabitUseCase,
            toggleHabitCompletionUseCase,
            setHabitProgressUseCase
        )

        viewModel.setHabitProgress(99L, 0.25f)
        advanceUntilIdle()

        assertEquals(1, repository.setProgressCalls.size)
        val call = repository.setProgressCalls.first()
        assertEquals(99L, call.habitId)
        assertEquals(0.25f, call.fraction)
    }
}