@file:OptIn(
    kotlinx.serialization.ExperimentalSerializationApi::class,
    kotlinx.serialization.InternalSerializationApi::class
)

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
    val winnerIsPlayerOne: Boolean? = null, // null if no winner or match incomplete
    val matchSessionId: String = "", // groups multiple saves of the same match
    // New: track last completed set games to avoid showing 0-0 right after set ends
    val lastCompletedSetP1Games: Int = 0,
    val lastCompletedSetP2Games: Int = 0,
    // New: track all completed sets
    val completedSets: List<SetResult> = emptyList()
) {
    companion object {
        fun fromGameState(
            gameState: GameState,
            matchDurationSeconds: Long = 0,
            isMatchCompleted: Boolean = false,
            matchSessionId: String? = null
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

            val now = System.currentTimeMillis()
            val id = now.toString()
            val matchHistory = MatchHistory(
                id = id,
                date = now,
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
                winnerIsPlayerOne = winnerIsPlayerOne,
                matchSessionId = matchSessionId ?: id, // fallback ensures old entries still group uniquely
                lastCompletedSetP1Games = gameState.lastCompletedSetP1Games,
                lastCompletedSetP2Games = gameState.lastCompletedSetP2Games,
                completedSets = gameState.completedSets
            )

            return matchHistory
        }
    }

    fun getFormattedScore(): String {
        return when (gameMode) {
            GameMode.MEXICANO -> {
                val scoreText = "Score: $playerOneScore - $playerTwoScore"
                if (isMatchCompleted && winnerIsPlayerOne != null) {
                    val winner = if (winnerIsPlayerOne == true) "P1 Won" else "P2 Won"
                    "$scoreText\n$winner"
                } else if (!isMatchCompleted) {
                    "$scoreText\nIncomplete"
                } else {
                    scoreText
                }
            }
            GameMode.VINNARBANA -> {
                if (completedSets.isNotEmpty()) {
                    // For individual set display (when completedSets has only 1 item)
                    if (completedSets.size == 1) {
                        val set = completedSets.first()
                        val winner = if (set.p1Games > set.p2Games) "P1 Won" else "P2 Won"
                        "Set: ${set.p1Games} - ${set.p2Games}\n$winner"
                    } else {
                        // For multiple sets (fallback to original logic)
                        val setsText = completedSets.mapIndexed { idx, set -> "Set ${idx + 1}: ${set.p1Games} - ${set.p2Games}" }.joinToString("\n")
                        
                        // Add current in-progress set if match is incomplete and has games
                        val currentSetText = if (!isMatchCompleted && (playerOneGamesWon > 0 || playerTwoGamesWon > 0)) {
                            val currentSetNumber = completedSets.size + 1
                            "\nSet $currentSetNumber: $playerOneGamesWon - $playerTwoGamesWon (in progress)"
                        } else ""
                        
                        "Sets: $playerOneSetsWon - $playerTwoSetsWon\n$setsText$currentSetText"
                    }
                } else {
                    // Handle incomplete sets (no completed sets but has current games)
                    if (!isMatchCompleted && (playerOneGamesWon > 0 || playerTwoGamesWon > 0)) {
                        "Set: $playerOneGamesWon - $playerTwoGamesWon\nIncomplete"
                    } else {
                        // Fallback for older matches or matches without completed sets data
                        val displayP1Games = if (playerOneGamesWon == 0 && playerTwoGamesWon == 0 &&
                            (lastCompletedSetP1Games > 0 || lastCompletedSetP2Games > 0)) lastCompletedSetP1Games else playerOneGamesWon
                        val displayP2Games = if (playerOneGamesWon == 0 && playerTwoGamesWon == 0 &&
                            (lastCompletedSetP1Games > 0 || lastCompletedSetP2Games > 0)) lastCompletedSetP2Games else playerTwoGamesWon

                        if (playerOneSetsWon > 0 || playerTwoSetsWon > 0) {
                            "Sets: $playerOneSetsWon - $playerTwoSetsWon\nGames: $displayP1Games - $displayP2Games"
                        } else {
                            "Games: $displayP1Games - $displayP2Games"
                        }
                    }
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