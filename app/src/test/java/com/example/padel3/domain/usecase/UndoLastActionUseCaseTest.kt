package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UndoLastActionUseCaseTest {

    private lateinit var useCase: UndoLastActionUseCase

    @Before
    fun setUp() {
        useCase = UndoLastActionUseCase()
    }

    @Test
    fun `undo with empty history returns null`() {
        // Given
        val emptyHistory = emptyList<GameState>()

        // When
        val (restoredState, newHistory) = useCase.execute(emptyHistory)

        // Then
        assertNull(restoredState)
        assertTrue(newHistory.isEmpty())
    }

    @Test
    fun `undo with single state in history`() {
        // Given
        val previousState = GameState(
            playerOneScore = 15,
            playerTwoScore = 0,
            gameMode = GameMode.VINNARBANA
        )
        val history = listOf(previousState)

        // When
        val (restoredState, newHistory) = useCase.execute(history)

        // Then
        assertNotNull(restoredState)
        assertEquals(15, restoredState!!.playerOneScore)
        assertEquals(0, restoredState.playerTwoScore)
        assertEquals(false, restoredState.canUndo) // Should be false since no more history
        assertTrue(newHistory.isEmpty())
    }

    @Test
    fun `undo with multiple states in history`() {
        // Given
        val state1 = GameState(playerOneScore = 0, playerTwoScore = 0)
        val state2 = GameState(playerOneScore = 15, playerTwoScore = 0)
        val state3 = GameState(playerOneScore = 15, playerTwoScore = 15)
        val history = listOf(state1, state2, state3)

        // When
        val (restoredState, newHistory) = useCase.execute(history)

        // Then
        assertNotNull(restoredState)
        assertEquals(15, restoredState!!.playerOneScore)
        assertEquals(15, restoredState.playerTwoScore)
        assertEquals(true, restoredState.canUndo) // Should be true since history still has items
        assertEquals(2, newHistory.size) // Should have 2 items remaining
    }

    @Test
    fun `canUndo returns false for empty history`() {
        // Given
        val emptyHistory = emptyList<GameState>()

        // When
        val result = useCase.canUndo(emptyHistory)

        // Then
        assertFalse(result)
    }

    @Test
    fun `canUndo returns true for non-empty history`() {
        // Given
        val history = listOf(GameState())

        // When
        val result = useCase.canUndo(history)

        // Then
        assertTrue(result)
    }
}