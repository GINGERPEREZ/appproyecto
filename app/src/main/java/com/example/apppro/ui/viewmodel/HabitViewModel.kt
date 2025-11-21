/// MVVVM Modelo vista modelo
package com.example.apppro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.apppro.domain.model.Habit
import com.example.apppro.domain.model.HabitProgress
import com.example.apppro.domain.model.HabitStatus
import com.example.apppro.domain.usecase.AddHabitUseCase
import com.example.apppro.domain.usecase.ObserveHabitProgressUseCase
import com.example.apppro.domain.usecase.ObserveHabitsUseCase
import com.example.apppro.domain.usecase.ObserveOverallProgressUseCase
import com.example.apppro.domain.usecase.SetHabitProgressUseCase
import com.example.apppro.domain.usecase.ToggleHabitCompletionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(
    observeHabitsUseCase: ObserveHabitsUseCase,
    observeHabitProgressUseCase: ObserveHabitProgressUseCase,
    observeOverallProgressUseCase: ObserveOverallProgressUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val setHabitProgressUseCase: SetHabitProgressUseCase
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = observeHabitsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val habitProgresses: StateFlow<List<HabitProgress>> = observeHabitProgressUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val overallProgress: StateFlow<Float> = observeOverallProgressUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val hasPendingHabits: StateFlow<Boolean> = habitProgresses
        .map { progressList -> progressList.any { it.status != HabitStatus.COMPLETED } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    var selectedHabitId by mutableStateOf<Long?>(null)
        private set

    fun selectHabit(id: Long?) {
        selectedHabitId = id
    }

    fun habitProgressFor(habitId: Long): HabitProgress? = habitProgresses.value.firstOrNull { it.habitId == habitId }

    fun addHabit(name: String, windowDays: Int = 7) {
        viewModelScope.launch {
            val habit = Habit(id = 0, name = name, windowDays = windowDays)
            addHabitUseCase(habit)
        }
    }

    fun toggleCompletion(habitId: Long, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            toggleHabitCompletionUseCase(habitId, date)
        }
    }

    fun setHabitProgress(habitId: Long, fraction: Float) {
        viewModelScope.launch {
            setHabitProgressUseCase(habitId, fraction)
        }
    }
}

class HabitViewModelFactory(
    private val observeHabitsUseCase: ObserveHabitsUseCase,
    private val observeHabitProgressUseCase: ObserveHabitProgressUseCase,
    private val observeOverallProgressUseCase: ObserveOverallProgressUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val toggleHabitCompletionUseCase: ToggleHabitCompletionUseCase,
    private val setHabitProgressUseCase: SetHabitProgressUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(
                observeHabitsUseCase,
                observeHabitProgressUseCase,
                observeOverallProgressUseCase,
                addHabitUseCase,
                toggleHabitCompletionUseCase,
                setHabitProgressUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
