package com.example.padel3.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MatchHistory(
    val id: String,
    val date: Long, // Timestamp
    val gameMode: GameMode,
    val playerOneScore: Int,
    val playerTwoScore: Int,
    val playerOneGamesWon: Int = 0,
    val playerTwoGamesWon: Int = 0,
    val playerOneSetsWon: Int = 0,
    val playerTwoSetsWon: Int = 0,
    val mexicanoMatchLimit: Int? = null,
    val setsToWinMatch: Int = 1,
    val scoringVariant: ScoringVariant = ScoringVariant.ADVANTAGE,
    val matchDurationSeconds: Long = 0,
    val isMatchCompleted: Boolean = false,
    val winnerIsPlayerOne: Boolean? = null // null if no winner or match incomplete
) {
    companion object {
        fun fromGameState(
            gameState: GameState,
            matchDurationSeconds: Long = 0,
            isMatchCompleted: Boolean = false
        ): MatchHistory {
            val winnerIsPlayerOne = when {
                !isMatchCompleted -> null
                gameState.gameMode == GameMode.MEXICANO -> {
                    gameState.playerOneScore > gameState.playerTwoScore
                }
                gameState.gameMode == GameMode.VINNARBANA -> {
                    gameState.playerOneSetsWon > gameState.playerTwoSetsWon
                }
                else -> null
            }

            return MatchHistory(
                id = System.currentTimeMillis().toString(),
                date = System.currentTimeMillis(),
                gameMode = gameState.gameMode,
                playerOneScore = gameState.playerOneScore,
                playerTwoScore = gameState.playerTwoScore,
                playerOneGamesWon = gameState.playerOneGamesWon,
                playerTwoGamesWon = gameState.playerTwoGamesWon,
                playerOneSetsWon = gameState.playerOneSetsWon,
                playerTwoSetsWon = gameState.playerTwoSetsWon,
                mexicanoMatchLimit = if (gameState.gameMode == GameMode.MEXICANO) gameState.mexicanoMatchLimit else null,
                setsToWinMatch = gameState.setsToWinMatch,
                scoringVariant = gameState.scoringVariant,
                matchDurationSeconds = matchDurationSeconds,
                isMatchCompleted = isMatchCompleted,
                winnerIsPlayerOne = winnerIsPlayerOne
            )
        }
    }

    fun getFormattedScore(): String {
        return when (gameMode) {
            GameMode.MEXICANO -> "$playerOneScore - $playerTwoScore"
            GameMode.VINNARBANA -> {
                if (playerOneSetsWon > 0 || playerTwoSetsWon > 0) {
                    "Sets: $playerOneSetsWon - $playerTwoSetsWon, Games: $playerOneGamesWon - $playerTwoGamesWon"
                } else {
                    "Games: $playerOneGamesWon - $playerTwoGamesWon"
                }
            }
        }
    }

    fun getFormattedDuration(): String {
        val hours = matchDurationSeconds / 3600
        val minutes = (matchDurationSeconds % 3600) / 60
        val seconds = matchDurationSeconds % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun getWinnerText(): String {
        return when (winnerIsPlayerOne) {
            true -> "Player 1 Won"
            false -> "Player 2 Won"
            null -> "Incomplete"
        }
    }
}