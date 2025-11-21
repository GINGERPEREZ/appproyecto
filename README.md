# MetaDiaria

MetaDiaria es una app Android construida con Jetpack Compose que ayuda a registrar y completar hábitos diarios. Combina una pantalla principal con tarjetas, sliders para ajustar progreso, un resumen semanal y recordatorios nocturnos basados en el nivel de luz.

## Qué contiene cada carpeta

```
appproyecto/
├── build.gradle.kts          # build root: plugins, repositorios y configuraciones compartidas
├── gradle/                   # wrapper de Gradle (versionado) y libs.versions.toml
├── settings.gradle.kts       # incluye el módulo `:app`
├── gradle.properties        # ajustes generales de la construcción (JVM args, etc.)
└── app/                      # módulo Android principal
    ├── build.gradle.kts      # dependencias específicas del app (Compose, Material3, DataStore, etc.)
    ├── proguard-rules.pro    # reglas para ofuscar en builds release
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml      # declara `MainActivity` y permisos pertinentes
        │   ├── java/com/example/apppro/  # código Kotlin organizado por capa
        │   │   ├── data/                # DataStore delegate, serialización y repositorio (`HabitDataStoreRepository`)
        │   │   ├── domain/              # modelos (`Habit`, `HabitProgress`), repositorio e `usecases`
        │   │   ├── ui/                  # tema, navegación, pantallas y componentes Compose
        │   │   ├── di/                  # `AppContainer` para inyectar repositorios y casos de uso
        │   │   └── MainActivity.kt       # arranca la app, crea el ViewModel y escucha el sensor de luz
        │   └── res/                     # recursos: colores, strings, mipmaps y Xml para backup rules
        └── test/                        # pruebas unitarias y utilidades (`MainDispatcherRule`, `RecordingHabitRepository`)
```

## Descripción de las capas

- **Data**: `DataStoreModule` expone el delegado para Preferences DataStore, `DataStoreUtil` serializa la lista de hábitos y `HabitDataStoreRepository` expone CRUD (insertar, actualizar, togglear completado, fijar progreso) aislando la persistencia.
- **Domain**: modela `Habit`, `HabitProgress`, `HabitStatus` y ofrece `progressOn()` para calcular fracciones. `HabitRepository` define la interfaz, mientras que los casos de uso (`AddHabitUseCase`, `ToggleHabitCompletionUseCase`, etc.) encapsulan la lógica antes de llegar al repositorio.
- **UI**:
  - `ui/theme`: esquema Material3 (colores claro/oscuro) para toda la app.
  - `ui/components/HabitItem.kt`: función reusable que dibuja tarjeta de hábito con slider y animaciones.
  - `ui/screens`: pantallas principales (`HabitsScreen`, `AddHabitScreen`, y `ProgressScreen`). `ProgressScreen` ya usa el mismo `HabitViewModel` para mostrar datos reales y el resumen semanal.
  - `ui/navigation/NavGraph.kt`: define `Destinations` y navigation graph de Jetpack Compose.
  - `ui/viewmodel/HabitViewModel.kt`: expone `StateFlow`s para la lista de hábitos, progreso total (`overallProgress`), progresos individuales y también una señal `hasPendingHabits` para saber si hay tareas por completar.
- **DI**: `AppContainer` arma el repositorio y los casos de uso para entregarlos al `HabitViewModelFactory`.
- **MainActivity**: inicia el tema, la navegación y además registra un `SensorEventListener` para el sensor de luz. Cuando detecta oscuridad y el ViewModel reporta hábitos pendientes, muestra un `AlertDialog` y dispara una notificación (`NotificationCompat`) recordando terminar las tareas.

## Flujo de uso

1. `HabitsScreen` muestra la lista de hábitos desde `HabitViewModel`. Cada tarjeta permite marcar completado (tap) o ajustar el progreso con el slider que llama a `setHabitProgress`.
2. `AddHabitScreen` permite crear un nuevo hábito con nombre y ventana fija (7 días por defecto).
3. `ProgressScreen` resume el progreso semanal con un gráfico y tarjetas que muestran el porcentaje y permiten togglear/debater el progreso real.
4. Al caer la noche (lux < 10), `MainActivity` abre un diálogo y lanza una notificación si todavía existen hábitos incompletos, ayudando al usuario a cerrar pendientes antes de dormir.

## Tests

- `app/src/test/java/.../HabitViewModelTest.kt` verifica que `setHabitProgress` delegate correctamente al repositorio.
- `RecordingHabitRepository` y `MainDispatcherRule` ayudan en los tests simulando repositorio y controlando el dispatcher principal.

## Comandos útiles (PowerShell)

```powershell
# Construir (debug)
./gradlew.bat assembleDebug

# Ejecutar tests unitarios
./gradlew.bat test

# Instalar en dispositivo/emulador conectado
./gradlew.bat installDebug
```

## Notas adicionales

- La persistencia es local, ligera y sin base de datos pesada; usa `Preferences DataStore` con serialización manual.
- El ViewModel expone flujos reactivos (`StateFlow`) para mantener sincronizada toda la UI.
- Si quieres preparar un artefacto para distribución, puedes extender `build.gradle.kts` con signingConfigs + proguard adicional para release.
