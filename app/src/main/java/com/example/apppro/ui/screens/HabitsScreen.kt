package com.example.apppro.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apppro.domain.model.HabitStatus
import com.example.apppro.ui.components.HabitItem
import com.example.apppro.ui.viewmodel.HabitViewModel
import java.time.LocalDate

@Composable
fun HabitsScreen(
    viewModel: HabitViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onAdd: () -> Unit,
    onShowProgress: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_media_previous),
                                contentDescription = "Volver"
                            )
                        }
                        Text(text = "MetaDiaria", style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val overall by viewModel.overallProgress.collectAsState()
                    LinearProgressIndicator(
                        progress = { overall.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isDarkTheme) "Tema oscuro" else "Tema claro",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(checked = isDarkTheme, onCheckedChange = onThemeToggle)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onShowProgress,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Progreso")
                        }
                        Button(onClick = onAdd) {
                            Text("Añadir")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val habits by viewModel.habits.collectAsState()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(habits, key = { it.id }) { habit ->
                    val isSelected = viewModel.selectedHabitId == habit.id
                    val habitProgress = viewModel.habitProgressFor(habit.id)
                    val statusText = when (habitProgress?.status) {
                        HabitStatus.COMPLETED -> "Completado"
                        HabitStatus.IN_PROGRESS -> "En progreso"
                        else -> "Pendiente"
                    }
                    HabitItem(
                        habit = habit,
                        isCompleted = (habitProgress?.status == HabitStatus.COMPLETED),
                        onToggle = { viewModel.toggleCompletion(habit.id, LocalDate.now()) },
                        onProgressChange = { viewModel.setHabitProgress(habit.id, it) },
                        isSelected = isSelected,
                        statusText = statusText,
                        progress = habitProgress?.progressFraction ?: 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}
