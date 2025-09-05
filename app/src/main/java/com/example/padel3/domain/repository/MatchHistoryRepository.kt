package com.example.padel3.domain.repository

import com.example.padel3.domain.model.MatchHistory
import kotlinx.coroutines.flow.Flow

interface MatchHistoryRepository {
    suspend fun saveMatch(match: MatchHistory)
    suspend fun getAllMatches(): List<MatchHistory>
    fun observeAllMatches(): Flow<List<MatchHistory>>
    suspend fun deleteMatch(matchId: String)
    suspend fun clearAllHistory()
    suspend fun getMatchById(matchId: String): MatchHistory?
}