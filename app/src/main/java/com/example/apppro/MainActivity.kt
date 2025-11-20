package com.example.apppro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apppro.di.AppContainer
import com.example.apppro.ui.navigation.AppNavHost
import com.example.apppro.ui.theme.APPPROTheme
import com.example.apppro.ui.viewmodel.HabitViewModel

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appContainer = AppContainer(this)
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            val habitViewModel: HabitViewModel = viewModel(factory = appContainer.habitViewModelFactory)
            APPPROTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        viewModel = habitViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}