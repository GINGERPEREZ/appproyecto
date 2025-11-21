package com.example.apppro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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

/**
 * Actividad principal que configura el tema, el ViewModel y el grafo de navegación.
 */
class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appContainer = AppContainer(this)
        setContent {
            // Conserva la preferencia de modo oscuro incluso tras recomposiciones.
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
    private val habitViewModel: HabitViewModel by lazy {
        ViewModelProvider(this, appContainer.habitViewModelFactory)[HabitViewModel::class.java]
    }

    private val lightSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val lux = event.values.firstOrNull() ?: return
            handleLightLevel(lux)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appContainer = AppContainer(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        notificationManager = NotificationManagerCompat.from(this)
        createNotificationChannel()

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            val hasPendingHabits by habitViewModel.hasPendingHabits.collectAsState(initial = false)
            val showNightAlert by nightAlertState

            APPPROTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        viewModel = habitViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                if (showNightAlert && hasPendingHabits) {
                    AlertDialog(
                        onDismissRequest = { nightAlertState.value = false },
                        confirmButton = {
                            Button(onClick = { nightAlertState.value = false }) {
                                Text("Entendido")
                            }
                        },
                        title = { Text("Recordatorio nocturno") },
                        text = { Text("Es de noche y tienes tareas pendientes. ¿Las completas antes de descansar?") }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.also { sensor ->
            sensorManager.registerListener(lightSensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(lightSensorListener)
        super.onPause()
    }

    private fun handleLightLevel(lux: Float) {
        val isNight = lux < NIGHT_THRESHOLD_LUX
        if (!isNight) {
            nightModeTriggered = false
            return
        }
        if (!nightModeTriggered && habitViewModel.hasPendingHabits.value) {
            nightModeTriggered = true
            runOnUiThread { nightAlertState.value = true }
            sendNightReminderNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NIGHT_NOTIFICATION_CHANNEL,
                "Recordatorios nocturnos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Se avisa por la noche si quedan tareas sin completar"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun sendNightReminderNotification() {
        val notification = NotificationCompat.Builder(this, NIGHT_NOTIFICATION_CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Es de noche")
            .setContentText("Tienes tareas pendientes. Termínalas antes de dormir.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NIGHT_NOTIFICATION_ID, notification)
    }
}