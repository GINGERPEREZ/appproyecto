Plan de alto nivel

Diagnóstico inicial – Mapear responsabilidades actuales (UI, ViewModel, estado, persistencia) para identificar cruces de capas.
Definir la arquitectura limpia – Establecer las capas (Presentation / UI, Domain, Data), interfaces, modelos y dependencias.
Rediseñar componentes – Migrar código a las nuevas capas en fases controladas (presentación primero, luego dominio, finalmente datos/persistencia).
Soporte y pruebas – Añadir tests unitarios/lógicos por capa y documentar cómo validar cada segmento tras los cambios.
Fase 1: Diagnóstico y base común
Objetivo: Entender qué hace cada archivo (HabitViewModel, screens, componentes, DataStore).
Resultado esperado: Lista de responsabilidades actuales y dependencias directas (por ejemplo, HabitsScreen accede a la lista, al ViewModel, a MutableState y a DataStore).
Checklist:
Registrar qué lógica vive en cada clase/módulo.
Detectar dependencias directas de la UI hacia la persistencia (ya que deben invertirse).
Fase 2: Diseño de la Clean Architecture
Capa Domain:
Crear entidades (Habit, HabitProgress, TaskStatus).
Definir casos de uso (AddHabit, ToggleHabitCompletion, GetHabitsFlow, ComputeOverallProgress).
Interfaces (portas) que describan lo que el dominio necesita del exterior (repositorios, relojes, etc.).
Capa Data:
Implementar repositorios concretos (HabitRepositoryImpl) que cumplan interfaces del dominio.
Centralizar la persistencia en un DataStore o base de datos (actualmente DataStore), exponiendo flows/sealed states.
Gestionar DTOs (si es necesario) y mapeo hacia entidades del dominio.
Capa Presentation:
ViewModels solo reciben use cases e interfaces del dominio.
HabitsScreen, AddHabitScreen, ProgressScreen consumen estados emitidos por el ViewModel (flujo inmutable).
Los componentes (HabitItem, ProgressChart, etc.) deben recibir solo datos y callbacks sin saber de la lógica de negocio.
Fase 3: Pasos de migración progresiva
Crear interfaces de repositorio y casos de uso.
Conectar HabitViewModel a los casos de uso:
ViewModel expone StateFlow<HabitsUiState> y StateFlow<ProgressState>.
Implementar collectAsState() en Compose, evitando mutableStateOf dentro del ViewModel.
Asegurar fuentes reactivas:
Configurar repository.getHabits() que devuelva Flow<List<Habit>>.
Los casos de uso transforman ese flujo en estados de comportamiento (completado, en progreso).
Refactorizar pantallas:
La UI solo trabaja con datos ya formateados (nombres, estados, porcentajes).
Reemplazar cualquier lógica de progreso en Compose por funciones del dominio.
Fase 4: Infraestructura y pruebas
Testing:
Tests unitarios para casos de uso y ViewModel (Mockear repositorio, validar estados).
Tests de integración para flujo completo (ViewModel + StateFlow + estado).
Documentación:
Describir cómo añadir nuevas funcionalidades (por ejemplo, nuevos casos de uso, nuevos repositorios).
Establecer patrones de naming/ubicación.
Fase 5: Iteraciones y validación
Migración incremental: mover una pantalla/CU por vez para no romper todo.
Verificación post-cambio:
Ejecutar ./gradlew assembleDebug.
Validar en emulador/dispositivo que las pantallas muestran datos y botones sin crash.
Observación de logs para asegurarse de que los flows se recomponen correctamente.
Recomendaciones adicionales
Dependencia inversion: usar constructor injection donde sea posible, facilitando pruebas.
Modularización opcional: en etapas avanzadas, separar en módulos Gradle (:domain, :data, :ui).
Observabilidad: habilitar logging o capturas de estado para diagnosticar futuros problemas.
