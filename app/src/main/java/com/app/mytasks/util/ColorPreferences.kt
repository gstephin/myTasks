package com.app.mytasks.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings_preferences")

class ColorPreferences(private val context: Context) {

    companion object {
        private val PRIMARY_COLOR_KEY = intPreferencesKey("primary_color")
        const val DEFAULT_COLOR = 0xFF6200EE.toInt()
    }

    val primaryColor: Flow<Int> = context.dataStore.data
        .map { preferences ->
            (preferences[PRIMARY_COLOR_KEY] as? Number)?.toInt() ?: DEFAULT_COLOR
        }


    suspend fun savePrimaryColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[PRIMARY_COLOR_KEY] = color
        }
    }
}