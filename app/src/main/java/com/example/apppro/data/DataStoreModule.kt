package com.example.apppro.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Delegate de Preferences DataStore para almacenar configuraciones y hábitos.
val Context.dataStore by preferencesDataStore(name = "metadiaria_prefs")
