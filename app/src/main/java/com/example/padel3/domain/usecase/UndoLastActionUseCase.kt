package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameState

class UndoLastActionUseCase {
    
    fun execute(history: List<GameState>): Pair<GameState?, List<GameState>> {
        if (history.isEmpty()) {
            return null to history
        }
        
        val newHistory = history.toMutableList()
        val previousState = newHistory.removeLastOrNull()
        
        val restoredState = previousState?.copy(
            canUndo = newHistory.isNotEmpty()
        )
        
        return restoredState to newHistory
    }
    
    fun canUndo(history: List<GameState>): Boolean {
        return history.isNotEmpty()
    }
}