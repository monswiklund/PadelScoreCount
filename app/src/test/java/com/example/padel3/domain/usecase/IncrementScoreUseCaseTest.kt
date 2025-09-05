package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState
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
}