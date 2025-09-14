package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameState

class ResetGameUseCase {
    
    fun resetGame(currentState: GameState): GameState {
        // Only reset everything if both scores are 0-0 initially (meaning we're resetting from 0-0)
        val shouldResetEverything = currentState.playerOneScore == 0 && currentState.playerTwoScore == 0
        
        return if (shouldResetEverything) {
            println("DEBUG: resetGame - doing full reset, clearing completedSets (had ${currentState.completedSets.size} sets)")
            currentState.copy(
                playerOneScore = GameState.POINTS_INITIAL,
                playerTwoScore = GameState.POINTS_INITIAL,
                playerOneGamesWon = 0,
                playerTwoGamesWon = 0,
                playerOneSetsWon = 0,
                playerTwoSetsWon = 0,
                gameWinSequence = emptyList(),
                // Clear last completed set snapshot when resetting everything
                lastCompletedSetP1Games = 0,
                lastCompletedSetP2Games = 0,
                completedSets = emptyList()
            )
        } else {
            // Just reset the current game scores, preserve sets and games
            println("DEBUG: resetGame - preserving completedSets (${currentState.completedSets.size} sets)")
            currentState.copy(
                playerOneScore = GameState.POINTS_INITIAL,
                playerTwoScore = GameState.POINTS_INITIAL
            )
        }
    }
    
    fun resetMatch(currentState: GameState, showModeSelector: Boolean = true): GameState {
        println("DEBUG: resetMatch - clearing completedSets (had ${currentState.completedSets.size} sets)")
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
            canUndo = false,
            gameWinSequence = emptyList(),
            // Clear last completed set snapshot for new match
            lastCompletedSetP1Games = 0,
            lastCompletedSetP2Games = 0,
            completedSets = emptyList()
        )
    }
    
    fun setGameMode(currentState: GameState, mode: com.example.padel3.domain.model.GameMode): GameState {
        println("DEBUG: setGameMode called - this will clear completedSets via resetMatch!")
        val resetState = resetMatch(currentState, showModeSelector = false)
        return resetState.copy(
            gameMode = mode,
            showServeSelector = mode == com.example.padel3.domain.model.GameMode.VINNARBANA
        )
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
    
    fun setSetsToWinMatch(currentState: GameState, sets: Int): GameState {
        val clampedSets = sets.coerceIn(GameState.MIN_SETS_TO_WIN, GameState.MAX_SETS_TO_WIN)
        return currentState.copy(setsToWinMatch = clampedSets)
    }
}