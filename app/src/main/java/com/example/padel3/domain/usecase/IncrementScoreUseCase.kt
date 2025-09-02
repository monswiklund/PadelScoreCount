package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState

class IncrementScoreUseCase {
    
    fun execute(currentState: GameState, isPlayerOne: Boolean): GameState {
        return when (currentState.gameMode) {
            GameMode.VINNARBANA -> handleVinnarbanaScoringIncrement(currentState, isPlayerOne)
            GameMode.MEXICANO -> handleMexicanoScoringIncrement(currentState, isPlayerOne)
        }
    }
    
    private fun handleVinnarbanaScoringIncrement(state: GameState, isPlayerOne: Boolean): GameState {
        if (state.isTieBreak) {
            return handleTieBreakIncrement(state, isPlayerOne)
        }
        
        val (playerScore, otherPlayerScore) = if (isPlayerOne) {
            state.playerOneScore to state.playerTwoScore
        } else {
            state.playerTwoScore to state.playerOneScore
        }
        
        val newScore = when (playerScore) {
            GameState.POINTS_INITIAL -> GameState.POINTS_FIRST_STEP
            GameState.POINTS_FIRST_STEP -> GameState.POINTS_SECOND_STEP
            GameState.POINTS_SECOND_STEP -> GameState.POINTS_THIRD_STEP
            GameState.POINTS_THIRD_STEP -> {
                // Player wins the game - award game and reset scores
                return awardGame(state, isPlayerOne)
            }
            else -> playerScore
        }
        
        return if (isPlayerOne) {
            state.copy(playerOneScore = newScore)
        } else {
            state.copy(playerTwoScore = newScore)
        }
    }
    
    private fun handleTieBreakIncrement(state: GameState, isPlayerOne: Boolean): GameState {
        val newPlayerOneTieBreak = if (isPlayerOne) state.playerOneTieBreakPoints + 1 else state.playerOneTieBreakPoints
        val newPlayerTwoTieBreak = if (isPlayerOne) state.playerTwoTieBreakPoints else state.playerTwoTieBreakPoints + 1
        
        val totalPoints = newPlayerOneTieBreak + newPlayerTwoTieBreak
        val shouldSwitchServer = totalPoints % 2 == 1
        
        val updatedState = state.copy(
            playerOneTieBreakPoints = newPlayerOneTieBreak,
            playerTwoTieBreakPoints = newPlayerTwoTieBreak,
            isPlayerOneServing = if (shouldSwitchServer) !state.isPlayerOneServing else state.isPlayerOneServing,
            gameWinSequence = state.gameWinSequence + isPlayerOne
        )
        
        // Check for tiebreak win
        return checkTieBreakWin(updatedState, isPlayerOne)
    }
    
    private fun handleMexicanoScoringIncrement(state: GameState, isPlayerOne: Boolean): GameState {
        val newPlayerOneScore = if (isPlayerOne) state.playerOneScore + 1 else state.playerOneScore
        val newPlayerTwoScore = if (isPlayerOne) state.playerTwoScore else state.playerTwoScore + 1
        
        val totalPoints = newPlayerOneScore + newPlayerTwoScore
        
        val updatedState = state.copy(
            playerOneScore = newPlayerOneScore,
            playerTwoScore = newPlayerTwoScore
        )
        
        // Auto-reset if match limit reached
        return if (totalPoints >= state.mexicanoMatchLimit) {
            updatedState.copy(
                playerOneScore = GameState.POINTS_INITIAL,
                playerTwoScore = GameState.POINTS_INITIAL
            )
        } else {
            updatedState
        }
    }
    
    private fun awardGame(state: GameState, isPlayerOne: Boolean): GameState {
        val newPlayerOneGames = if (isPlayerOne) state.playerOneGamesWon + 1 else state.playerOneGamesWon
        val newPlayerTwoGames = if (isPlayerOne) state.playerTwoGamesWon else state.playerTwoGamesWon + 1
        
        val gameResetState = state.copy(
            playerOneScore = GameState.POINTS_INITIAL,
            playerTwoScore = GameState.POINTS_INITIAL,
            playerOneGamesWon = newPlayerOneGames,
            playerTwoGamesWon = newPlayerTwoGames,
            gameWinSequence = state.gameWinSequence + isPlayerOne,
            isPlayerOneServing = !state.isPlayerOneServing // Alternate serve every game
        )
        
        return checkAndProcessSetWin(gameResetState, isPlayerOne)
    }
    
    private fun checkTieBreakWin(state: GameState, isPlayerOne: Boolean): GameState {
        val player1Points = state.playerOneTieBreakPoints
        val player2Points = state.playerTwoTieBreakPoints
        
        val winningPoints = GameState.TIEBREAK_POINTS_TO_WIN
        val minDifference = GameState.TIEBREAK_MIN_POINT_DIFFERENCE
        
        if (isPlayerOne && player1Points >= winningPoints && (player1Points - player2Points) >= minDifference) {
            // Player 1 wins tiebreak
            return state.copy(
                playerOneSetsWon = state.playerOneSetsWon + 1,
                playerOneGamesWon = 0,
                playerTwoGamesWon = 0,
                isTieBreak = false,
                playerOneTieBreakPoints = 0,
                playerTwoTieBreakPoints = 0,
                showServeSelector = false,
                gameWinSequence = emptyList()
            )
        } else if (!isPlayerOne && player2Points >= winningPoints && (player2Points - player1Points) >= minDifference) {
            // Player 2 wins tiebreak
            return state.copy(
                playerTwoSetsWon = state.playerTwoSetsWon + 1,
                playerOneGamesWon = 0,
                playerTwoGamesWon = 0,
                isTieBreak = false,
                playerOneTieBreakPoints = 0,
                playerTwoTieBreakPoints = 0,
                showServeSelector = false,
                gameWinSequence = emptyList()
            )
        }
        
        return state
    }
    
    private fun checkAndProcessSetWin(state: GameState, isPlayerOne: Boolean): GameState {
        // Check for 6-6 tiebreak scenario
        if (state.playerOneGamesWon == 6 && state.playerTwoGamesWon == 6) {
            return state.copy(
                isTieBreak = true,
                showServeSelector = true
            )
        }
        
        val gamesNeeded = GameState.GAMES_TO_WIN_SET
        val minDifference = GameState.MIN_GAME_DIFFERENCE
        
        if (isPlayerOne &&
            state.playerOneGamesWon >= gamesNeeded &&
            state.playerOneGamesWon - state.playerTwoGamesWon >= minDifference) {
            // Player 1 wins set
            return state.copy(
                playerOneSetsWon = state.playerOneSetsWon + 1,
                playerOneGamesWon = 0,
                playerTwoGamesWon = 0,
                gameWinSequence = emptyList()
            )
        } else if (!isPlayerOne &&
            state.playerTwoGamesWon >= gamesNeeded &&
            state.playerTwoGamesWon - state.playerOneGamesWon >= minDifference) {
            // Player 2 wins set
            return state.copy(
                playerTwoSetsWon = state.playerTwoSetsWon + 1,
                playerOneGamesWon = 0,
                playerTwoGamesWon = 0,
                gameWinSequence = emptyList()
            )
        }
        
        return state
    }
}