package com.example.apppro.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.apppro.ui.viewmodel.HabitViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * Refactored ProgressScreen
 * - Defines a Task data class with emoji mapping
 * - Shows a weekly bar chart (Mon..Sun)
 * - Renders a list of Task cards with progress, action icons (complete/edit/delete)
 * - Uses `remember` + `mutableStateListOf` for real-time state updates
 * - Confetti animation implemented in Compose (simple particle system)
 */

data class Task(
    val id: Long,
    var title: String,
    var progress: Float, // current value
    var goal: Float, // target value
    var streak: Int = 0,
    var isCompleted: Boolean = false
) {
    val emoji: String
        get() {
            val t = title.lowercase()
            return when {
                "agua" in t || "beber" in t -> "💧"
                "dormir" in t || "sueño" in t -> "😴"
                "estudi" in t || "leer" in t || "libro" in t -> "📚"
                "ejercicio" in t || "correr" in t || "gym" in t -> "🏃‍♂️"
                "meditar" in t || "mind" in t -> "🧘‍♀️"
                "comer" in t || "aliment" in t -> "🍎"
                else -> "⚡"
            }
        }

    fun percent(): Int = if (goal <= 0f) 0 else min(100f, (progress / goal * 100f)).toInt()
}

@Composable
fun ProgressScreen(
    initialTasks: List<Task> = emptyList(),
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    // State: task list
    val tasks = remember {
        mutableStateListOf<Task>().apply {
            if (initialTasks.isEmpty()) {
                add(Task(1, "Beber agua", 6f, 8f, 2, false))
                add(Task(2, "Dormir bien", 7f, 8f, 3, false))
                add(Task(3, "Estudiar Kotlin", 3f, 5f, 1, false))
            } else addAll(initialTasks)
        }
    }

    // Confetti state
    var confettiActive by remember { mutableStateOf(false) }
    var confettiKey by remember { mutableStateOf(0) }

    // store previous progress to allow toggle/undo of complete action
    val previousProgress = remember { mutableStateMapOf<Long, Float>() }

    Surface(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Resumen Semanal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Weekly chart: compute a simple 7-day series using tasks' average progress
                val avg = if (tasks.isEmpty()) 0f else tasks.map { (it.progress / max(1f, it.goal)).coerceIn(0f, 1f) }.average().toFloat()
                val weekly = (0..6).map { idx ->
                    // create a slight deterministic variation so bars differ across days
                    (avg + 0.12f * sin(idx.toFloat() + avg * 3f)).coerceIn(0f, 1f)
                }

                WeeklyBarChart(values = weekly, modifier = Modifier.fillMaxWidth().height(120.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Tareas", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                // Tasks list
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tasks.forEachIndexed { index, task ->
                        TaskCard(
                            task = task,
                            onComplete = {
                                // Implementación estricta "Marcar como Visto": completar al instante
                                val was = task.isCompleted
                                if (!was) {
                                    // Guardar progreso previo para posible deshacer
                                    previousProgress[task.id] = task.progress
                                    // Crear copia actualizada y reemplazar en la lista para forzar recomposición
                                    val completed = task.copy(
                                        progress = task.goal,
                                        isCompleted = true,
                                        streak = task.streak + 1
                                    )
                                    tasks[index] = completed
                                    // Disparar confeti
                                    confettiActive = true
                                    confettiKey += 1
                                } else {
                                    // Toggle: restaurar progreso previo (o 0) y desmarcar completado
                                    val prev = previousProgress.remove(task.id) ?: 0f
                                    val restored = task.copy(
                                        progress = prev,
                                        isCompleted = false,
                                        streak = max(0, task.streak - 1)
                                    )
                                    tasks[index] = restored
                                }
                            },
                            onDelete = {
                                tasks.removeAt(index)
                            },
                            onEdit = { updated ->
                                // reemplazo directo al editar
                                tasks[index] = updated
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(onClick = onBack) { Text("Volver") }
            }

            // Confetti overlay
            if (confettiActive) {
                ConfettiOverlay(key = confettiKey) { confettiActive = false }
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(values: List<Float>, modifier: Modifier = Modifier) {
    // values expected in 0..1 for Mon..Sun
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        values.forEachIndexed { i, v ->
            val animated = animateFloatAsState(targetValue = v, animationSpec = tween(durationMillis = 600))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    // bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((animated.value * 80).dp)
                            .align(Alignment.BottomCenter)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                val day = when (i) {
                    0 -> "Lun"
                    1 -> "Mar"
                    2 -> "Mie"
                    3 -> "Jue"
                    4 -> "Vie"
                    5 -> "Sab"
                    else -> "Dom"
                }
                Text(text = day, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEdit by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(task.title) }
    var editedGoal by remember { mutableStateOf(task.goal.toString()) }

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = task.emoji, modifier = Modifier.padding(end = 8.dp))
                    Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                }

                Text(text = "Racha: ${task.streak}", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // progress bar + percent
            val pct = if (task.goal <= 0f) 0f else (task.progress / task.goal).coerceIn(0f, 1f)
            val animated by animateFloatAsState(targetValue = pct, animationSpec = TweenSpec(durationMillis = 600, easing = LinearEasing))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(progress = animated, modifier = Modifier.weight(1f).height(10.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "${(animated * 100).toInt()}%", modifier = Modifier.width(50.dp), textAlign = TextAlign.End)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // action icons
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { onComplete() }) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "Completar")
                }
                IconButton(onClick = { showEdit = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }

    if (showEdit) {
        AlertDialog(onDismissRequest = { showEdit = false }, confirmButton = {
            Button(onClick = {
                val g = editedGoal.toFloatOrNull() ?: task.goal
                task.title = editedTitle
                task.goal = g
                onEdit(task)
                showEdit = false
            }) { Text("Guardar") }
        }, dismissButton = {
            Button(onClick = { showEdit = false }) { Text("Cancelar") }
        }, text = {
            Column {
                OutlinedTextField(value = editedTitle, onValueChange = { editedTitle = it }, label = { Text("Título") })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = editedGoal, onValueChange = { editedGoal = it }, label = { Text("Meta (número)") })
            }
        })
    }
}

// --- Simple confetti overlay implemented with Compose (no external libs) ---
@Composable
private fun ConfettiOverlay(key: Int, onFinished: () -> Unit) {
    // spawn particles on key change
    val scope = rememberCoroutineScope()
    val particles = remember(key) {
        List(24) {
            Particle(
                x = Random.nextFloat(),
                y = -Random.nextFloat() * 0.3f,
                vx = (Random.nextFloat() - 0.5f) * 0.6f,
                vy = 0.8f + Random.nextFloat() * 0.6f,
                size = (6 + Random.nextInt(10)).dp,
                color = Color(
                    red = Random.nextInt(256) / 255f,
                    green = Random.nextInt(256) / 255f,
                    blue = Random.nextInt(256) / 255f,
                    alpha = 1f
                ),
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    // animate particles for ~1.6s
    LaunchedEffect(key) {
        scope.launch {
            // wait then finish
            delay(1600)
            onFinished()
        }
    }

    // Draw overlay canvas
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val t = (System.currentTimeMillis() % 100000L) / 1000f
                particles.forEachIndexed { idx, p ->
                // simple physics: updated position by time + random seed
                val lifetime = 1.6f
                val phase = ((t + idx) % lifetime) / lifetime
                val px = (p.x + p.vx * phase) * w
                val py = (p.y + p.vy * phase) * h

                // rotation wobble (draw simple rects for confetti)
                rotate(p.rotation + phase * 360f, pivot = Offset(px, py)) {
                    drawRect(color = p.color, topLeft = Offset(px, py), size = Size(p.size.toPx(), p.size.toPx()))
                }
            }
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val size: Dp,
    val color: Color,
    val rotation: Float
)

