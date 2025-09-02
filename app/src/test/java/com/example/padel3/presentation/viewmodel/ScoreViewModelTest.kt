package com.example.padel3.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.GameState
import com.example.padel3.domain.usecase.IncrementScoreUseCase
import com.example.padel3.domain.usecase.ResetGameUseCase
import com.example.padel3.domain.usecase.UndoLastActionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ScoreViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var mockIncrementScoreUseCase: IncrementScoreUseCase
    
    @Mock
    private lateinit var mockResetGameUseCase: ResetGameUseCase
    
    @Mock
    private lateinit var mockUndoLastActionUseCase: UndoLastActionUseCase

    private lateinit var viewModel: ScoreViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        viewModel = ScoreViewModel(
            incrementScoreUseCase = mockIncrementScoreUseCase,
            resetGameUseCase = mockResetGameUseCase,
            undoLastActionUseCase = mockUndoLastActionUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default GameState`() = runTest {
        // Then
        val currentState = viewModel.uiState.value
        assertEquals(GameState(), currentState)
    }

    @Test
    fun `incrementPlayerOneScore calls use case and updates state`() = runTest {
        // Given
        val initialState = GameState()
        val expectedState = GameState(playerOneScore = 15)
        `when`(mockIncrementScoreUseCase.execute(any(), eq(true))).thenReturn(expectedState)

        // When
        viewModel.incrementPlayerOneScore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockIncrementScoreUseCase).execute(any(), eq(true))
        assertEquals(15, viewModel.uiState.value.playerOneScore)
    }

    @Test
    fun `incrementPlayerTwoScore calls use case and updates state`() = runTest {
        // Given
        val initialState = GameState()
        val expectedState = GameState(playerTwoScore = 15)
        `when`(mockIncrementScoreUseCase.execute(any(), eq(false))).thenReturn(expectedState)

        // When
        viewModel.incrementPlayerTwoScore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockIncrementScoreUseCase).execute(any(), eq(false))
        assertEquals(15, viewModel.uiState.value.playerTwoScore)
    }

    @Test
    fun `resetGame calls use case and updates state`() = runTest {
        // Given
        val currentState = GameState(playerOneScore = 30, playerTwoScore = 15)
        val expectedState = GameState(playerOneScore = 0, playerTwoScore = 0)
        `when`(mockResetGameUseCase.resetGame(any())).thenReturn(expectedState)

        // When
        viewModel.resetGame()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockResetGameUseCase).resetGame(any())
    }

    @Test
    fun `setGameMode calls use case and updates state`() = runTest {
        // Given
        val expectedState = GameState(gameMode = GameMode.MEXICANO, showModeSelector = false)
        `when`(mockResetGameUseCase.setGameMode(any(), eq(GameMode.MEXICANO))).thenReturn(expectedState)

        // When
        viewModel.setGameMode(GameMode.MEXICANO)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockResetGameUseCase).setGameMode(any(), eq(GameMode.MEXICANO))
        assertEquals(GameMode.MEXICANO, viewModel.uiState.value.gameMode)
        assertEquals(false, viewModel.uiState.value.showModeSelector)
    }

    @Test
    fun `undoLastAction with empty history does nothing`() = runTest {
        // Given
        `when`(mockUndoLastActionUseCase.execute(any())).thenReturn(Pair(null, emptyList()))

        // When
        viewModel.undoLastAction()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockUndoLastActionUseCase).execute(any())
        // State should remain unchanged
        assertEquals(GameState(), viewModel.uiState.value)
    }

    private fun <T> any(): T {
        return org.mockito.ArgumentMatchers.any()
    }

    private fun <T> eq(value: T): T {
        return org.mockito.ArgumentMatchers.eq(value)
    }
}