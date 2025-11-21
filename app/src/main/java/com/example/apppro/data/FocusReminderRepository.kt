package com.example.apppro.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.apppro.data.dataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface FocusReminderStore {
    val focusReminderEnabled: Flow<Boolean>
    suspend fun setFocusReminderEnabled(enabled: Boolean): Unit
}

class FocusReminderRepository(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FocusReminderStore {
    override val focusReminderEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FOCUS_REMINDER_ENABLED_KEY] ?: true }

    override suspend fun setFocusReminderEnabled(enabled: Boolean): Unit = withContext(ioDispatcher) {
        context.dataStore.edit { prefs ->
            prefs[FOCUS_REMINDER_ENABLED_KEY] = enabled
        }
    }
}
