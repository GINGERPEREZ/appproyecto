package com.example.apppro.data

import android.util.Base64
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.apppro.domain.model.Habit
import java.time.LocalDate

val HABITS_KEY = stringPreferencesKey("habits_serialized")

/**
 * Serializa los hábitos para guardarlos en DataStore usando Base64 para los nombres.
 */
fun serializeHabits(habits: List<Habit>): String {
    return habits.joinToString(";;") { h ->
        val encodedName = Base64.encodeToString(h.name.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        val dates = h.completions.joinToString(",") { it.toString() }
        "${h.id}|${encodedName}|${dates}"
    }
}

/**
 * Reconstruye los hábitos desde la cadena guardada, manejando fechas ISO y decodificando nombres.
 */
fun deserializeHabits(s: String): List<Habit> {
    if (s.isBlank()) return emptyList()
    return s.split(";;").mapNotNull { item ->
        val parts = item.split("|")
        if (parts.size < 3) return@mapNotNull null
        val id = parts[0].toLongOrNull() ?: return@mapNotNull null
        val name = try {
            String(Base64.decode(parts[1], Base64.NO_WRAP), Charsets.UTF_8)
        } catch (e: Exception) {
            parts[1]
        }
        val datesPart = parts[2]
        val completions = if (datesPart.isBlank()) emptyList() else datesPart.split(",").map { LocalDate.parse(it) }
        Habit(id = id, name = name, completions = completions)
    }
}
