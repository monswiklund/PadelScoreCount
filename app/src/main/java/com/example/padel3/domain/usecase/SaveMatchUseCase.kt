package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameState
import com.example.padel3.domain.model.MatchHistory
import com.example.padel3.domain.repository.MatchHistoryRepository

class SaveMatchUseCase(
    private val matchHistoryRepository: MatchHistoryRepository
) {
    suspend fun execute(
        gameState: GameState, 
        matchDurationSeconds: Long = 0,
        isMatchCompleted: Boolean = false,
        matchSessionId: String? = null
    ): Result<Unit> {
        return try {
            val matchHistory = MatchHistory.fromGameState(
                gameState = gameState,
                matchDurationSeconds = matchDurationSeconds,
                isMatchCompleted = isMatchCompleted,
                matchSessionId = matchSessionId
            )
            matchHistoryRepository.saveMatch(matchHistory)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun execute(matchHistory: MatchHistory): Result<Unit> {
        return try {
            matchHistoryRepository.saveMatch(matchHistory)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}