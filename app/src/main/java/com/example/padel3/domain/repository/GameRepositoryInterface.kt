package com.example.padel3.domain.repository

import com.example.padel3.domain.model.GameState
import com.example.padel3.domain.model.GameMode
import kotlinx.coroutines.flow.Flow

interface GameRepositoryInterface {
    
    // Game state management
    suspend fun saveGameState(gameState: GameState)
    suspend fun getGameState(): GameState
    fun observeGameState(): Flow<GameState>
    
    // Game history for undo functionality
    suspend fun saveStateToHistory(gameState: GameState)
    suspend fun getStateHistory(): List<GameState>
    suspend fun clearHistory()
    
    // Preferences
    suspend fun saveLastUsedGameMode(mode: GameMode)
    suspend fun getLastUsedGameMode(): GameMode
    suspend fun saveMexicanoLimit(limit: Int)
    suspend fun getMexicanoLimit(): Int
    
    // Reset functionality
    suspend fun clearAllData()
}