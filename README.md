MetaDiaria — Minimal habit tracker (Debug build)

Descripción

MetaDiaria es una app Android minimalista para marcar hábitos diarios simples (ej. "Tomar agua"). Está diseñada con Jetpack Compose y usa Preferences DataStore para persistencia local.

Estructura clave

- `app/src/main/java/com/example/apppro/ui/screens` — pantallas: `HabitsScreen`, `AddHabitScreen`, `ProgressScreen`.
- `app/src/main/java/com/example/apppro/ui/viewmodel/HabitViewModel.kt` — estado en memoria y API para togglear/completar hábitos.
- `app/src/main/java/com/example/apppro/data` — utilidades de `DataStore` para persistencia.

Instrucciones rápidas (Windows PowerShell)

1) Compilar debug:

```powershell
# Desde la raíz del proyecto
.\gradlew.bat assembleDebug
```

2) Instalar en emulador/dispositivo conectado (requiere adb/emulador activo):

```powershell
# Instalar la build debug en el dispositivo/emulador
.\gradlew.bat installDebug
```

3) Ejecutar desde Android Studio si prefieres ver logs y debug.

Notas

- La persistencia usa Preferences DataStore con una serialización simple.
- Las casillas en `ProgressScreen` son interactivas y persistentes.
- Si quieres un APK listo para distribuir, puedo añadir configuración para `release` y firmarlo.
