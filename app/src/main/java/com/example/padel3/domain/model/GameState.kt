package com.example.padel3.domain.model

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
    val showModeSelector: Boolean = true,
    val showServeSelector: Boolean = false,
    val canUndo: Boolean = false,
    val gameWinSequence: List<Boolean> = emptyList() // true = player 1 won, false = player 2 won
) {
    companion object {
        const val POINTS_INITIAL = 0
        const val POINTS_FIRST_STEP = 15
        const val POINTS_SECOND_STEP = 30
        const val POINTS_THIRD_STEP = 40

        const val GAMES_TO_WIN_SET = 6
        const val MIN_GAME_DIFFERENCE = 2
        const val SETS_TO_WIN_MATCH = 3

        const val TIEBREAK_POINTS_TO_WIN = 7
        const val TIEBREAK_MIN_POINT_DIFFERENCE = 2
    }
}