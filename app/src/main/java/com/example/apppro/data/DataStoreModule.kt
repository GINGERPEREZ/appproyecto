package com.example.apppro.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Preferences DataStore delegate
val Context.dataStore by preferencesDataStore(name = "metadiaria_prefs")
