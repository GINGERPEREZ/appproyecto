package com.example.apppro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apppro.ui.viewmodel.HabitViewModel

@Composable
fun AddHabitScreen(viewModel: HabitViewModel, onDone: () -> Unit, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Añadir Hábito")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del hábito") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Button(onClick = {
                if (name.isNotBlank()) {
                    viewModel.addHabit(name.trim())
                    onDone()
                }
            }, modifier = Modifier.padding(top = 12.dp)) {
                Text("Guardar")
            }
        }
    }
}
