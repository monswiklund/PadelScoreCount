package com.example.padel3.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.padel3.domain.model.GameMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_preferences")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val LAST_GAME_MODE = stringPreferencesKey("last_game_mode")
        private val MEXICANO_LIMIT = intPreferencesKey("mexicano_limit")
    }
    
    suspend fun saveLastUsedGameMode(mode: GameMode) {
        context.dataStore.edit { preferences ->
            preferences[LAST_GAME_MODE] = mode.name
        }
    }
    
    suspend fun getLastUsedGameMode(): GameMode {
        return context.dataStore.data.map { preferences ->
            val modeName = preferences[LAST_GAME_MODE] ?: GameMode.VINNARBANA.name
            try {
                GameMode.valueOf(modeName)
            } catch (e: IllegalArgumentException) {
                GameMode.VINNARBANA
            }
        }.first()
    }
    
    fun observeLastUsedGameMode(): Flow<GameMode> {
        return context.dataStore.data.map { preferences ->
            val modeName = preferences[LAST_GAME_MODE] ?: GameMode.VINNARBANA.name
            try {
                GameMode.valueOf(modeName)
            } catch (e: IllegalArgumentException) {
                GameMode.VINNARBANA
            }
        }
    }
    
    suspend fun saveMexicanoLimit(limit: Int) {
        context.dataStore.edit { preferences ->
            preferences[MEXICANO_LIMIT] = limit
        }
    }
    
    suspend fun getMexicanoLimit(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[MEXICANO_LIMIT] ?: 24
        }.first()
    }
    
    fun observeMexicanoLimit(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[MEXICANO_LIMIT] ?: 24
        }
    }
    
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

