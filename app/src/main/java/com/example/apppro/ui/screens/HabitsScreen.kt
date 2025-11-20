package com.example.apppro.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apppro.ui.components.HabitItem
import com.example.apppro.ui.viewmodel.HabitViewModel
import java.time.LocalDate

@Composable
fun HabitsScreen(
    viewModel: HabitViewModel,
    onAdd: () -> Unit,
    onShowProgress: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Title + progress take remaining space
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Back button (optional)
                        androidx.compose.material3.IconButton(onClick = onBack) {
                            androidx.compose.material3.Icon(
                                painter = painterResource(id = android.R.drawable.ic_media_previous),
                                contentDescription = "Volver"
                            )
                        }
                        Text(text = "MetaDiaria", style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val overall by viewModel.overallProgress.collectAsState()
                    LinearProgressIndicator(progress = overall, modifier = Modifier.fillMaxWidth().height(8.dp))
                }

                // Buttons grouped to the end
                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Button(onClick = onShowProgress, modifier = Modifier.padding(end = 8.dp)) { Text("Progreso") }
                    androidx.compose.material3.Button(onClick = onAdd) { Text("Añadir") }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.habits, key = { it.id }) { habit ->
                    val isSelected = viewModel.selectedHabitId == habit.id
                    val prog = viewModel.habitProgress(habit.id)
                    val statusText = when {
                        prog >= 1f -> "Completado"
                        prog > 0f -> "En progreso"
                        else -> "Pendiente"
                    }
                    HabitItem(
                        habit = habit,
                        isCompleted = prog >= 1f,
                        onToggle = { viewModel.toggleCompletion(habit.id, LocalDate.now()) },
                        isSelected = isSelected,
                        statusText = statusText,
                        progress = prog,
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
