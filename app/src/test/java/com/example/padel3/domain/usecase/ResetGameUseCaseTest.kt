package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ResetGameUseCaseTest {

    private lateinit var useCase: ResetGameUseCase

    @Before
    fun setUp() {
        useCase = ResetGameUseCase()
    }

    @Test
    fun `reset game scores only`() {
        // Given
        val state = GameState(
            playerOneScore = 30,
            playerTwoScore = 15,
            playerOneGamesWon = 2,
            playerTwoGamesWon = 1,
            gameMode = GameMode.VINNARBANA
        )

        // When
        val result = useCase.resetGame(state)

        // Then - Only scores should reset, games and sets preserved
        assertEquals(0, result.playerOneScore)
        assertEquals(0, result.playerTwoScore)
        assertEquals(2, result.playerOneGamesWon)
        assertEquals(1, result.playerTwoGamesWon)
        assertEquals(GameMode.VINNARBANA, result.gameMode)
    }

    @Test
    fun `reset entire match`() {
        // Given
        val state = GameState(
            playerOneScore = 30,
            playerTwoScore = 15,
            playerOneGamesWon = 3,
            playerTwoGamesWon = 2,
            playerOneSetsWon = 1,
            playerTwoSetsWon = 0,
            gameMode = GameMode.VINNARBANA
        )

        // When
        val result = useCase.resetMatch(state, showModeSelector = true)

        // Then - Everything should be reset
        assertEquals(0, result.playerOneScore)
        assertEquals(0, result.playerTwoScore)
        assertEquals(0, result.playerOneGamesWon)
        assertEquals(0, result.playerTwoGamesWon)
        assertEquals(0, result.playerOneSetsWon)
        assertEquals(0, result.playerTwoSetsWon)
        assertEquals(true, result.showModeSelector)
        assertEquals(false, result.canUndo)
    }

    @Test
    fun `set game mode resets match`() {
        // Given
        val state = GameState(
            playerOneScore = 15,
            playerTwoScore = 30,
            playerOneGamesWon = 1,
            gameMode = GameMode.VINNARBANA
        )

        // When
        val result = useCase.setGameMode(state, GameMode.MEXICANO)

        // Then
        assertEquals(GameMode.MEXICANO, result.gameMode)
        assertEquals(0, result.playerOneScore)
        assertEquals(0, result.playerTwoScore)
        assertEquals(0, result.playerOneGamesWon)
        assertEquals(false, result.showModeSelector)
    }

    @Test
    fun `set mexicano limit`() {
        // Given
        val state = GameState(mexicanoMatchLimit = 24)

        // When
        val result = useCase.setMexicanoLimit(state, 30)

        // Then
        assertEquals(30, result.mexicanoMatchLimit)
    }

    @Test
    fun `set initial server`() {
        // Given
        val state = GameState(
            isPlayerOneServing = false,
            showServeSelector = true
        )

        // When
        val result = useCase.setInitialServer(state, isPlayerOne = true)

        // Then
        assertEquals(true, result.isPlayerOneServing)
        assertEquals(false, result.showServeSelector)
    }
}