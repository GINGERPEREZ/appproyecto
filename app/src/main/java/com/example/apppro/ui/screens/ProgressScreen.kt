package com.example.apppro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.apppro.domain.model.Habit
import com.example.apppro.domain.model.HabitProgress
import com.example.apppro.domain.model.HabitStatus
import com.example.apppro.ui.viewmodel.HabitViewModel
import kotlin.math.max

@Composable
fun ProgressScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val habits by viewModel.habits.collectAsState()
    val progresses by viewModel.habitProgresses.collectAsState()
    val overallProgress by viewModel.overallProgress.collectAsState()

    val progressMap = remember(progresses) {
        progresses.associateBy { it.habitId }
    }

    val weeklySeries = remember(overallProgress) {
        (0..6).map { index ->
            (overallProgress + 0.05f * (index % 3)).coerceIn(0f, 1f)
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Resumen Semanal", style = MaterialTheme.typography.headlineSmall)
                Text(text = "${(overallProgress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            WeeklyBarChart(values = weeklySeries, modifier = Modifier
                .fillMaxWidth()
                .height(140.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Tareas activas", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitProgressCard(
                        habit = habit,
                        progress = progressMap[habit.id],
                        onToggleCompletion = { viewModel.toggleCompletion(habit.id) },
                        onProgressChange = { viewModel.setHabitProgress(habit.id, it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBack, modifier = Modifier.align(Alignment.End)) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(values: List<Float>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEachIndexed { index, value ->
            val barHeight = (value * 90f).coerceIn(0f, 90f)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(90.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = dayLabel(index), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

private fun dayLabel(index: Int) = when (index) {
    0 -> "Lun"
    1 -> "Mar"
    2 -> "Mié"
    3 -> "Jue"
    4 -> "Vie"
    5 -> "Sáb"
    else -> "Dom"
}

@Composable
private fun HabitProgressCard(
    habit: Habit,
    progress: HabitProgress?,
    onToggleCompletion: () -> Unit,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentFraction = progress?.progressFraction ?: 0f
    var sliderPosition by remember { mutableStateOf(currentFraction) }
    LaunchedEffect(currentFraction) {
        sliderPosition = currentFraction
    }

    val statusText = when (progress?.status) {
        HabitStatus.COMPLETED -> "Completado"
        HabitStatus.IN_PROGRESS -> "En progreso"
        else -> "Pendiente"
    }

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = habit.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = statusText, style = MaterialTheme.typography.labelSmall)
                }
                Text(
                    text = "${(sliderPosition * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = sliderPosition,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = { onProgressChange(sliderPosition) },
                valueRange = 0f..1f,
                steps = max(habit.windowDays - 1, 0),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ventana: ${habit.windowDays} días", style = MaterialTheme.typography.labelSmall)
                Button(onClick = onToggleCompletion) {
                    Text(text = when (progress?.status) {
                        HabitStatus.COMPLETED -> "Desmarcar"
                        else -> "Completar"
                    })
                }
            }
        }
    }
}

