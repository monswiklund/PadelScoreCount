package com.example.padel3.data.repository

import com.example.padel3.data.preferences.PreferencesManager
import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState
import com.example.padel3.domain.repository.GameRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GameRepositoryImpl(
    private val preferencesManager: PreferencesManager
) : GameRepositoryInterface {
    
    private var currentGameState = GameState()
    private val gameStateFlow = MutableStateFlow(currentGameState)
    private val historyList = mutableListOf<GameState>()
    
    override suspend fun saveGameState(gameState: GameState) {
        currentGameState = gameState
        gameStateFlow.value = gameState
    }
    
    override suspend fun getGameState(): GameState {
        return currentGameState
    }
    
    override fun observeGameState(): Flow<GameState> {
        return gameStateFlow
    }
    
    override suspend fun saveStateToHistory(gameState: GameState) {
        historyList.add(gameState)
        // Keep only the last 20 entries
        if (historyList.size > 20) {
            historyList.removeAt(0)
        }
    }
    
    override suspend fun getStateHistory(): List<GameState> {
        return historyList.toList()
    }
    
    override suspend fun clearHistory() {
        historyList.clear()
    }
    
    override suspend fun saveLastUsedGameMode(mode: GameMode) {
        preferencesManager.saveLastUsedGameMode(mode)
    }
    
    override suspend fun getLastUsedGameMode(): GameMode {
        return preferencesManager.getLastUsedGameMode()
    }
    
    override suspend fun saveMexicanoLimit(limit: Int) {
        preferencesManager.saveMexicanoLimit(limit)
    }
    
    override suspend fun getMexicanoLimit(): Int {
        return preferencesManager.getMexicanoLimit()
    }
    
    override suspend fun clearAllData() {
        currentGameState = GameState()
        gameStateFlow.value = currentGameState
        historyList.clear()
        preferencesManager.clearAll()
    }
}