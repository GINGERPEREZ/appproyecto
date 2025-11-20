package com.example.apppro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apppro.ui.screens.AddHabitScreen
import com.example.apppro.ui.screens.HabitsScreen
import com.example.apppro.ui.screens.ProgressScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apppro.ui.viewmodel.HabitViewModel

object Destinations {
    const val HABITS = "habits"
    const val ADD = "add"
    const val PROGRESS = "progress"
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm: HabitViewModel = viewModel()

    NavHost(navController = navController, startDestination = Destinations.HABITS, modifier = modifier) {
        composable(Destinations.HABITS) {
            HabitsScreen(viewModel = vm, onAdd = { navController.navigate(Destinations.ADD) }, onShowProgress = { navController.navigate(Destinations.PROGRESS) })
        }
        composable(Destinations.ADD) {
            AddHabitScreen(viewModel = vm, onDone = { navController.navigateUp() })
        }
        composable(Destinations.PROGRESS) {
            ProgressScreen(onBack = { navController.navigateUp() })
        }
    }
}
