package com.example.padel3.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.MatchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_preferences")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val LAST_GAME_MODE = stringPreferencesKey("last_game_mode")
        private val MEXICANO_LIMIT = intPreferencesKey("mexicano_limit")
        private val MATCH_HISTORY = stringPreferencesKey("match_history")
        private const val MAX_HISTORY_SIZE = 50
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
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

    // Match History Methods
    suspend fun saveMatchToHistory(match: MatchHistory) {
        println("DEBUG: saveMatchToHistory called with match ID: ${match.id}")
        context.dataStore.edit { preferences ->
            val currentHistoryJson = preferences[MATCH_HISTORY] ?: "[]"
            val currentHistory = try {
                json.decodeFromString<List<MatchHistory>>(currentHistoryJson)
            } catch (e: Exception) {
                println("DEBUG: Error decoding current history: $e")
                emptyList()
            }

            println("DEBUG: Current history size: ${currentHistory.size}")

            // Add new match and maintain size limit
            val updatedHistory = (currentHistory + match)
                .sortedByDescending { it.date }
                .take(MAX_HISTORY_SIZE)

            println("DEBUG: Updated history size: ${updatedHistory.size}")
            preferences[MATCH_HISTORY] = json.encodeToString(updatedHistory)
            println("DEBUG: Match saved to DataStore successfully")
        }
    }

    suspend fun getMatchHistory(): List<MatchHistory> {
        return context.dataStore.data.map { preferences ->
            val historyJson = preferences[MATCH_HISTORY] ?: "[]"
            try {
                json.decodeFromString<List<MatchHistory>>(historyJson)
            } catch (e: Exception) {
                emptyList()
            }
        }.first()
    }

    fun observeMatchHistory(): Flow<List<MatchHistory>> {
        return context.dataStore.data.map { preferences ->
            val historyJson = preferences[MATCH_HISTORY] ?: "[]"
            try {
                json.decodeFromString<List<MatchHistory>>(historyJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun deleteMatchFromHistory(matchId: String) {
        context.dataStore.edit { preferences ->
            val currentHistoryJson = preferences[MATCH_HISTORY] ?: "[]"
            val currentHistory = try {
                json.decodeFromString<List<MatchHistory>>(currentHistoryJson)
            } catch (e: Exception) {
                emptyList()
            }

            val updatedHistory = currentHistory.filter { it.id != matchId }
            preferences[MATCH_HISTORY] = json.encodeToString(updatedHistory)
        }
    }

    suspend fun clearMatchHistory() {
        println("DEBUG: PreferencesManager clearMatchHistory called")
        context.dataStore.edit { preferences ->
            val hadHistory = preferences.contains(MATCH_HISTORY)
            preferences.remove(MATCH_HISTORY)
            println("DEBUG: Match history cleared, had history before: $hadHistory")
        }
    }

    suspend fun getMatchById(matchId: String): MatchHistory? {
        val history = getMatchHistory()
        return history.find { it.id == matchId }
    }
}

