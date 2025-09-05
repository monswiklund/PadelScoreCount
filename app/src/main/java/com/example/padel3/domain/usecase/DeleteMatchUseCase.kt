package com.example.padel3.domain.usecase

import com.example.padel3.domain.repository.MatchHistoryRepository

class DeleteMatchUseCase(
    private val matchHistoryRepository: MatchHistoryRepository
) {
    suspend fun execute(matchId: String): Result<Unit> {
        return try {
            matchHistoryRepository.deleteMatch(matchId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllHistory(): Result<Unit> {
        return try {
            matchHistoryRepository.clearAllHistory()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}