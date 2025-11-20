package com.example.apppro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apppro.ui.screens.AddHabitScreen
import com.example.apppro.ui.screens.HabitsScreen
import com.example.apppro.ui.screens.ProgressScreen
import com.example.apppro.ui.viewmodel.HabitViewModel

object Destinations {
    const val HABITS = "habits"
    const val ADD = "add"
    const val PROGRESS = "progress"
}

@Composable
fun AppNavHost(
    viewModel: HabitViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.HABITS, modifier = modifier) {
        composable(Destinations.HABITS) {
            HabitsScreen(
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onAdd = { navController.navigate(Destinations.ADD) },
                onShowProgress = { navController.navigate(Destinations.PROGRESS) }
            )
        }
        composable(Destinations.ADD) {
            AddHabitScreen(viewModel = viewModel, onDone = { navController.navigateUp() })
        }
        composable(Destinations.PROGRESS) {
            ProgressScreen(onBack = { navController.navigateUp() })
        }
    }
}
