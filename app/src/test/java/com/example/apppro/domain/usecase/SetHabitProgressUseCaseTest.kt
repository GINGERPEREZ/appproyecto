package com.example.apppro.domain.usecase

import com.example.apppro.testutil.RecordingHabitRepository
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SetHabitProgressUseCaseTest {
    @Test
    fun `delegates set progress calls to repository`() = runTest {
        val repo = RecordingHabitRepository()
        val today = LocalDate.of(2025, 11, 20)
        val useCase = SetHabitProgressUseCase(repo)

        useCase(habitId = 42L, progressFraction = 0.7f, today = today)

        assertEquals(1, repo.setProgressCalls.size)
        val call = repo.setProgressCalls.first()
        assertEquals(42L, call.habitId)
        assertEquals(0.7f, call.fraction)
        assertEquals(today, call.date)
    }
}