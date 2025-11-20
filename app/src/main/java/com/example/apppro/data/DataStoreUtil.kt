package com.example.apppro.data

import android.util.Base64
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.apppro.ui.viewmodel.Habit
import java.time.LocalDate

val HABITS_KEY = stringPreferencesKey("habits_serialized")

// Simple, safe serialization using Base64 for names and ISO dates for completions.
fun serializeHabits(habits: List<Habit>): String {
    return habits.joinToString(";;") { h ->
        val encodedName = Base64.encodeToString(h.name.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        val dates = h.completions.joinToString(",") { it.toString() }
        "${h.id}|${encodedName}|${dates}"
    }
}

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
        val completions = if (datesPart.isBlank()) mutableListOf() else datesPart.split(",").map { LocalDate.parse(it) }.toMutableList()
        Habit(id = id, name = name, completions = completions)
    }
}
