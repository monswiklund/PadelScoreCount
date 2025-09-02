package com.example.padel3.domain.model

sealed class ScoreEvent {
    object PlayerOneScoreIncrement : ScoreEvent()
    object PlayerTwoScoreIncrement : ScoreEvent()
    object ResetGame : ScoreEvent()
    object UndoLastAction : ScoreEvent()
    data class SetGameMode(val mode: GameMode) : ScoreEvent()
    data class SetMexicanoLimit(val limit: Int) : ScoreEvent()
    data class SetInitialServer(val isPlayerOne: Boolean) : ScoreEvent()
    object ResetMatch : ScoreEvent()
}