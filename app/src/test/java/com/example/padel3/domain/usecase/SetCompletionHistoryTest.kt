package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState
import com.example.padel3.domain.model.MatchHistory
import org.junit.Assert.assertEquals
import org.junit.Test

class SetCompletionHistoryTest {

    private val increment = IncrementScoreUseCase()

    @Test
    fun `normal set completion stores last set games and resets current games`() {
        // Given: Player 1 leading 5-0 games and at 40-0 in the game
        val initial = GameState(
            gameMode = GameMode.VINNARBANA,
            playerOneScore = GameState.POINTS_THIRD_STEP,
            playerTwoScore = GameState.POINTS_INITIAL,
            playerOneGamesWon = 5,
            playerTwoGamesWon = 0
        )

        // When: Player 1 wins the point -> wins game 6-0 and thus the set 6-0
        val after = increment.execute(initial, isPlayerOne = true)

        // Then: last set snapshot captured, current games reset
        assertEquals(6, after.lastCompletedSetP1Games)
        assertEquals(0, after.lastCompletedSetP2Games)
        assertEquals(0, after.playerOneGamesWon)
        assertEquals(0, after.playerTwoGamesWon)
        assertEquals(1, after.playerOneSetsWon)

        // And MatchHistory uses lastCompletedSet values when games are 0-0
        val history = MatchHistory.fromGameState(after)
        val formatted = history.getFormattedScore()
        // Expect to show last completed set games instead of 0-0
        assertEquals("Sets: 1 - 0\nGames: 6 - 0", formatted)
    }

    @Test
    fun `tiebreak set completion stores 7-6 as last set games`() {
        // Given: In a tiebreak, Player 2 leads 6-5 (next point ends tiebreak)
        val initial = GameState(
            gameMode = GameMode.VINNARBANA,
            isTieBreak = true,
            playerOneTieBreakPoints = 5,
            playerTwoTieBreakPoints = 6,
            playerOneSetsWon = 0,
            playerTwoSetsWon = 0
        )

        // When: Player 2 wins the next point -> wins tiebreak and the set 7-6
        val after = increment.execute(initial, isPlayerOne = false)

        // Then: last set snapshot captured as 6-7 and games reset
        assertEquals(6, after.lastCompletedSetP1Games)
        assertEquals(7, after.lastCompletedSetP2Games)
        assertEquals(0, after.playerOneGamesWon)
        assertEquals(0, after.playerTwoGamesWon)
        assertEquals(1, after.playerTwoSetsWon)

        // And MatchHistory uses lastCompletedSet values when games are 0-0
        val history = MatchHistory.fromGameState(after)
        val formatted = history.getFormattedScore()
        assertEquals("Sets: 0 - 1\nGames: 6 - 7", formatted)
    }
}

