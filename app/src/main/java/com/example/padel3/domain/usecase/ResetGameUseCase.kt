package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameState

class ResetGameUseCase {
    
    fun resetGame(currentState: GameState): GameState {
        return currentState.copy(
            playerOneScore = GameState.POINTS_INITIAL,
            playerTwoScore = GameState.POINTS_INITIAL
        )
    }
    
    fun resetMatch(currentState: GameState, showModeSelector: Boolean = true): GameState {
        return currentState.copy(
            playerOneScore = GameState.POINTS_INITIAL,
            playerTwoScore = GameState.POINTS_INITIAL,
            playerOneGamesWon = 0,
            playerTwoGamesWon = 0,
            playerOneSetsWon = 0,
            playerTwoSetsWon = 0,
            isTieBreak = false,
            playerOneTieBreakPoints = 0,
            playerTwoTieBreakPoints = 0,
            isPlayerOneServing = true,
            showModeSelector = showModeSelector,
            showServeSelector = false,
            canUndo = false
        )
    }
    
    fun setGameMode(currentState: GameState, mode: com.example.padel3.domain.model.GameMode): GameState {
        val resetState = resetMatch(currentState, showModeSelector = false)
        return resetState.copy(gameMode = mode)
    }
    
    fun setMexicanoLimit(currentState: GameState, limit: Int): GameState {
        return currentState.copy(mexicanoMatchLimit = limit)
    }
    
    fun setInitialServer(currentState: GameState, isPlayerOne: Boolean): GameState {
        return currentState.copy(
            isPlayerOneServing = isPlayerOne,
            showServeSelector = false
        )
    }
}