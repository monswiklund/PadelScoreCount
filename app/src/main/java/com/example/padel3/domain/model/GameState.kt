package com.example.padel3.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ScoringVariant {
    ADVANTAGE,    // Traditional advantage scoring
    GOLDEN_POINT  // Sudden death at deuce
}

enum class ResetAction {
    RESET_GAME,   // Reset current game only
    RESET_MATCH   // Reset entire match
}

@Serializable
data class SetResult(val p1Games: Int, val p2Games: Int)

data class GameState(
    val playerOneScore: Int = 0,
    val playerTwoScore: Int = 0,
    val playerOneGamesWon: Int = 0,
    val playerTwoGamesWon: Int = 0,
    val playerOneSetsWon: Int = 0,
    val playerTwoSetsWon: Int = 0,
    val isTieBreak: Boolean = false,
    val playerOneTieBreakPoints: Int = 0,
    val playerTwoTieBreakPoints: Int = 0,
    val isPlayerOneServing: Boolean = true,
    val gameMode: GameMode = GameMode.VINNARBANA,
    val mexicanoMatchLimit: Int = 24,
    val setsToWinMatch: Int = 1,
    val scoringVariant: ScoringVariant = ScoringVariant.ADVANTAGE,
    val isDeuceState: Boolean = false,
    val playerOneHasAdvantage: Boolean? = null, // null = no advantage, true = P1, false = P2
    val showMainMenu: Boolean = true,
    val showModeSelector: Boolean = false,
    val showServeSelector: Boolean = false,
    val showMatchHistory: Boolean = false,
    val showSaveConfirmationDialog: Boolean = false,
    val pendingResetAction: ResetAction? = null,
    val canUndo: Boolean = false,
    val gameWinSequence: List<Boolean> = emptyList(), // true = player 1 won, false = player 2 won
    // New: remember last completed set’s final games so history can show them
    val lastCompletedSetP1Games: Int = 0,
    val lastCompletedSetP2Games: Int = 0,
    // New: track all completed sets
    val completedSets: List<SetResult> = emptyList()
) {
    companion object {
        const val POINTS_INITIAL = 0
        const val POINTS_FIRST_STEP = 15
        const val POINTS_SECOND_STEP = 30
        const val POINTS_THIRD_STEP = 40

        const val GAMES_TO_WIN_SET = 6
        const val MIN_GAME_DIFFERENCE = 2
        const val SETS_TO_WIN_MATCH = 3
        const val MIN_SETS_TO_WIN = 1
        const val MAX_SETS_TO_WIN = 6

        const val TIEBREAK_POINTS_TO_WIN = 7
        const val TIEBREAK_MIN_POINT_DIFFERENCE = 2
    }
}