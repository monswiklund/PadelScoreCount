package com.example.padel3.domain.usecase

import com.example.padel3.domain.model.GameMode
import com.example.padel3.domain.model.MatchHistory
import com.example.padel3.domain.repository.MatchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class GetMatchHistoryUseCase(
    private val matchHistoryRepository: MatchHistoryRepository
) {
    
    suspend fun execute(): List<MatchHistory> {
        return matchHistoryRepository.getAllMatches()
    }

    fun observeMatchHistory(): Flow<List<MatchHistory>> {
        return matchHistoryRepository.observeAllMatches()
    }

    suspend fun getFilteredMatches(
        gameMode: GameMode? = null,
        onlyCompleted: Boolean = false,
        dateFrom: Long? = null,
        dateTo: Long? = null
    ): List<MatchHistory> {
        val allMatches = matchHistoryRepository.getAllMatches()
        
        return allMatches.filter { match ->
            // Filter by game mode
            if (gameMode != null && match.gameMode != gameMode) return@filter false
            
            // Filter by completion status
            if (onlyCompleted && !match.isMatchCompleted) return@filter false
            
            // Filter by date range
            if (dateFrom != null && match.date < dateFrom) return@filter false
            if (dateTo != null && match.date > dateTo) return@filter false
            
            true
        }
    }

    fun observeFilteredMatches(
        gameMode: GameMode? = null,
        onlyCompleted: Boolean = false,
        dateFrom: Long? = null,
        dateTo: Long? = null
    ): Flow<List<MatchHistory>> {
        return matchHistoryRepository.observeAllMatches().map { allMatches ->
            allMatches.filter { match ->
                // Filter by game mode
                if (gameMode != null && match.gameMode != gameMode) return@filter false
                
                // Filter by completion status
                if (onlyCompleted && !match.isMatchCompleted) return@filter false
                
                // Filter by date range
                if (dateFrom != null && match.date < dateFrom) return@filter false
                if (dateTo != null && match.date > dateTo) return@filter false
                
                true
            }
        }
    }

    suspend fun getMatchesForToday(): List<MatchHistory> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val startOfNextDay = calendar.timeInMillis
        
        return getFilteredMatches(
            dateFrom = startOfDay,
            dateTo = startOfNextDay
        )
    }

    suspend fun getMatchesForLastWeek(): List<MatchHistory> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val weekAgo = calendar.timeInMillis
        
        return getFilteredMatches(dateFrom = weekAgo)
    }

    suspend fun getMatchStatistics(): MatchStatistics {
        val allMatches = matchHistoryRepository.getAllMatches()
        val completedMatches = allMatches.filter { it.isMatchCompleted }
        
        val playerOneWins = completedMatches.count { it.winnerIsPlayerOne == true }
        val playerTwoWins = completedMatches.count { it.winnerIsPlayerOne == false }
        
        val vinnarbanMatches = completedMatches.filter { it.gameMode == GameMode.VINNARBANA }
        val mexicanoMatches = completedMatches.filter { it.gameMode == GameMode.MEXICANO }
        
        val averageDuration = if (completedMatches.isNotEmpty()) {
            completedMatches.map { it.matchDurationSeconds }.average().toLong()
        } else 0L
        
        return MatchStatistics(
            totalMatches = allMatches.size,
            completedMatches = completedMatches.size,
            playerOneWins = playerOneWins,
            playerTwoWins = playerTwoWins,
            vinnarbanMatches = vinnarbanMatches.size,
            mexicanoMatches = mexicanoMatches.size,
            averageMatchDurationSeconds = averageDuration
        )
    }
}

data class MatchStatistics(
    val totalMatches: Int,
    val completedMatches: Int,
    val playerOneWins: Int,
    val playerTwoWins: Int,
    val vinnarbanMatches: Int,
    val mexicanoMatches: Int,
    val averageMatchDurationSeconds: Long
) {
    fun getAverageMatchDurationFormatted(): String {
        val hours = averageMatchDurationSeconds / 3600
        val minutes = (averageMatchDurationSeconds % 3600) / 60
        val seconds = averageMatchDurationSeconds % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
}