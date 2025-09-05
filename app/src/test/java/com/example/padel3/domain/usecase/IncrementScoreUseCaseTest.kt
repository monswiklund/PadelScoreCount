package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState
import com.example.padel3.domain.model.ScoringVariant
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class IncrementScoreUseCaseTest {

    private lateinit var useCase: IncrementScoreUseCase

    @Before
    fun setUp() {
        useCase = IncrementScoreUseCase()
    }

    @Test
    fun `increment player one score in Vinnarbana mode`() {
        // Given
        val initialState = GameState(
            playerOneScore = 0,
            playerTwoScore = 0,
            gameMode = GameMode.VINNARBANA
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then
        assertEquals(15, result.playerOneScore)
        assertEquals(0, result.playerTwoScore)
    }

    @Test
    fun `increment player two score in Vinnarbana mode`() {
        // Given
        val initialState = GameState(
            playerOneScore = 0,
            playerTwoScore = 15,
            gameMode = GameMode.VINNARBANA
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = false)

        // Then
        assertEquals(0, result.playerOneScore)
        assertEquals(30, result.playerTwoScore)
    }

    @Test
    fun `increment player one score in Mexicano mode`() {
        // Given
        val initialState = GameState(
            playerOneScore = 5,
            playerTwoScore = 3,
            gameMode = GameMode.MEXICANO,
            mexicanoMatchLimit = 24
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then
        assertEquals(6, result.playerOneScore)
        assertEquals(3, result.playerTwoScore)
    }

    @Test
    fun `player gets 40 points when at 30 in Vinnarbana mode`() {
        // Given
        val initialState = GameState(
            playerOneScore = 30,
            playerTwoScore = 15,
            playerOneGamesWon = 0,
            playerTwoGamesWon = 0,
            gameMode = GameMode.VINNARBANA
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then
        assertEquals(40, result.playerOneScore) // Should be 40, not reset yet
        assertEquals(15, result.playerTwoScore)
        assertEquals(0, result.playerOneGamesWon) // Game not awarded yet
        assertEquals(0, result.playerTwoGamesWon)
    }

    @Test
    fun `award game when player wins from 40 in Vinnarbana mode`() {
        // Given
        val initialState = GameState(
            playerOneScore = 40,
            playerTwoScore = 15,
            playerOneGamesWon = 0,
            playerTwoGamesWon = 0,
            gameMode = GameMode.VINNARBANA
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then
        assertEquals(0, result.playerOneScore) // Score resets after game win
        assertEquals(0, result.playerTwoScore)
        assertEquals(1, result.playerOneGamesWon) // Game awarded
        assertEquals(0, result.playerTwoGamesWon)
    }

    @Test
    fun `stop scoring when Mexicano match limit reached`() {
        // Given
        val initialState = GameState(
            playerOneScore = 12,
            playerTwoScore = 11,
            gameMode = GameMode.MEXICANO,
            mexicanoMatchLimit = 24
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then - Should reach limit but not reset automatically
        assertEquals(13, result.playerOneScore)
        assertEquals(11, result.playerTwoScore)
    }

    @Test
    fun `prevent scoring beyond Mexicano match limit`() {
        // Given
        val initialState = GameState(
            playerOneScore = 12,
            playerTwoScore = 12,
            gameMode = GameMode.MEXICANO,
            mexicanoMatchLimit = 24
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then - Should not increment score since total is already at limit
        assertEquals(12, result.playerOneScore)
        assertEquals(12, result.playerTwoScore)
    }

    @Test
    fun `advantage scoring - first deuce gives advantage to scoring player`() {
        // Given
        val initialState = GameState(
            playerOneScore = 40,
            playerTwoScore = 40,
            gameMode = GameMode.VINNARBANA,
            scoringVariant = ScoringVariant.ADVANTAGE
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then
        assertTrue("Should be in deuce state", result.isDeuceState)
        assertTrue("Player 1 should have advantage", result.playerOneHasAdvantage == true)
        assertEquals(40, result.playerOneScore) // Scores stay at 40
        assertEquals(40, result.playerTwoScore)
        assertEquals(0, result.playerOneGamesWon) // No game won yet
    }

    @Test
    fun `advantage scoring - player with advantage wins game`() {
        // Given
        val initialState = GameState(
            playerOneScore = 40,
            playerTwoScore = 40,
            gameMode = GameMode.VINNARBANA,
            scoringVariant = ScoringVariant.ADVANTAGE,
            isDeuceState = true,
            playerOneHasAdvantage = true
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then
        assertEquals(0, result.playerOneScore) // Should reset after game win
        assertEquals(0, result.playerTwoScore)
        assertEquals(1, result.playerOneGamesWon) // Player 1 should have won game
        assertFalse("Should no longer be in deuce state", result.isDeuceState)
        assertNull("No one should have advantage", result.playerOneHasAdvantage)
    }

    @Test
    fun `advantage scoring - opponent scores to return to deuce`() {
        // Given
        val initialState = GameState(
            playerOneScore = 40,
            playerTwoScore = 40,
            gameMode = GameMode.VINNARBANA,
            scoringVariant = ScoringVariant.ADVANTAGE,
            isDeuceState = true,
            playerOneHasAdvantage = true
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = false)

        // Then
        assertTrue("Should remain in deuce state", result.isDeuceState)
        assertNull("No one should have advantage (back to deuce)", result.playerOneHasAdvantage)
        assertEquals(40, result.playerOneScore) // Scores stay at 40
        assertEquals(40, result.playerTwoScore)
        assertEquals(0, result.playerOneGamesWon) // No game won yet
    }

    @Test
    fun `golden point scoring - immediate win at 40-40`() {
        // Given
        val initialState = GameState(
            playerOneScore = 40,
            playerTwoScore = 40,
            gameMode = GameMode.VINNARBANA,
            scoringVariant = ScoringVariant.GOLDEN_POINT
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then
        assertEquals(0, result.playerOneScore) // Should reset after game win
        assertEquals(0, result.playerTwoScore)
        assertEquals(1, result.playerOneGamesWon) // Player 1 should have won game immediately
        assertFalse("Should not be in deuce state", result.isDeuceState)
        assertNull("No advantage in golden point", result.playerOneHasAdvantage)
    }

    @Test
    fun `deuce state resets when game is awarded`() {
        // Given
        val initialState = GameState(
            playerOneScore = 40,
            playerTwoScore = 40,
            gameMode = GameMode.VINNARBANA,
            scoringVariant = ScoringVariant.ADVANTAGE,
            isDeuceState = true,
            playerOneHasAdvantage = true
        )

        // When
        val result = useCase.execute(initialState, isPlayerOne = true)

        // Then - All deuce-related state should be reset
        assertFalse("Deuce state should be reset", result.isDeuceState)
        assertNull("Advantage should be reset", result.playerOneHasAdvantage)
        assertEquals(1, result.playerOneGamesWon)
        assertEquals(0, result.playerOneScore)
        assertEquals(0, result.playerTwoScore)
    }
}