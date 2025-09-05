package com.example.padel3.data.repository

import com.example.padel3.data.preferences.PreferencesManager
import com.example.padel3.domain.model.MatchHistory
import com.example.padel3.domain.repository.MatchHistoryRepository
import kotlinx.coroutines.flow.Flow

class MatchHistoryRepositoryImpl(
    private val preferencesManager: PreferencesManager
) : MatchHistoryRepository {

    override suspend fun saveMatch(match: MatchHistory) {
        preferencesManager.saveMatchToHistory(match)
    }

    override suspend fun getAllMatches(): List<MatchHistory> {
        return preferencesManager.getMatchHistory()
    }

    override fun observeAllMatches(): Flow<List<MatchHistory>> {
        return preferencesManager.observeMatchHistory()
    }

    override suspend fun deleteMatch(matchId: String) {
        preferencesManager.deleteMatchFromHistory(matchId)
    }

    override suspend fun clearAllHistory() {
        preferencesManager.clearMatchHistory()
    }

    override suspend fun getMatchById(matchId: String): MatchHistory? {
        return preferencesManager.getMatchById(matchId)
    }
}