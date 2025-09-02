package com.example.padel3.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

enum class GameMode {
    VINNARBANA,
    MEXICANO
}

/**
 * Dataklassen som representerar ett tillstånd i poängräkningen.
 */
data class ScoreState(
    val playerOneScore: Int,
    val playerTwoScore: Int,
    val playerOneGamesWon: Int,
    val playerTwoGamesWon: Int,
    val playerOneSetsWon: Int,
    val playerTwoSetsWon: Int,
    val isTieBreak: Boolean,
    val playerOneTieBreakPoints: Int,
    val playerTwoTieBreakPoints: Int,
    val isPlayerOneServing: Boolean = true,
    val gameMode: GameMode = GameMode.VINNARBANA,
    val mexicanoMatchLimit: Int = 24
)

/**
 * ViewModel för poängräkning i Padel-matchen.
 */
class ScoreViewModel : ViewModel() {
    companion object {
        const val POINTS_INITIAL = 0
        const val POINTS_FIRST_STEP = 15
        const val POINTS_SECOND_STEP = 30
        const val POINTS_THIRD_STEP = 40

        const val GAMES_TO_WIN_SET = 6
        const val MIN_GAME_DIFFERENCE = 2
        const val SETS_TO_WIN_MATCH = 3

        const val TIEBREAK_POINTS_TO_WIN = 7
        const val TIEBREAK_MIN_POINT_DIFFERENCE = 2
    }

    private val _playerOneScore = mutableStateOf(POINTS_INITIAL)
    private val _playerTwoScore = mutableStateOf(POINTS_INITIAL)
    private val _playerOneGamesWon = mutableStateOf(0)
    private val _playerTwoGamesWon = mutableStateOf(0)
    private val _playerOneSetsWon = mutableStateOf(0)
    private val _playerTwoSetsWon = mutableStateOf(0)

    // Tiebreak-relaterade tillstånd
    private val _isTieBreak = mutableStateOf(false)
    private val _playerOneTieBreakPoints = mutableStateOf(0)
    private val _playerTwoTieBreakPoints = mutableStateOf(0)

    // Serve-relaterade tillstånd
    private val _isPlayerOneServing = mutableStateOf(true) // Standard: röd (fiende) börjar serva
    private val _showServeSelector = mutableStateOf(false) // Visa serve-väljare när tiebreak startar
    
    // Game mode-relaterade tillstånd
    private val _gameMode = mutableStateOf(GameMode.VINNARBANA)
    private val _showModeSelector = mutableStateOf(true) // Visa mode-väljare vid start
    private val _mexicanoMatchLimit = mutableStateOf(24) // Standard: 24 poäng för Mexicano

    private val _stateHistory = mutableListOf<ScoreState>()
    private val _canUndo = mutableStateOf(false)

    val playerOneScore: State<Int> = _playerOneScore
    val playerTwoScore: State<Int> = _playerTwoScore
    val playerOneGamesWon: State<Int> = _playerOneGamesWon
    val playerTwoGamesWon: State<Int> = _playerTwoGamesWon
    val playerOneSetsWon: State<Int> = _playerOneSetsWon
    val playerTwoSetsWon: State<Int> = _playerTwoSetsWon
    val canUndo: State<Boolean> = _canUndo

    // Exponera tiebreak-tillstånd
    val isTieBreak: State<Boolean> = _isTieBreak
    val playerOneTieBreakPoints: State<Int> = _playerOneTieBreakPoints
    val playerTwoTieBreakPoints: State<Int> = _playerTwoTieBreakPoints

    // Exponera serve-tillstånd
    val isPlayerOneServing: State<Boolean> = _isPlayerOneServing
    val showServeSelector: State<Boolean> = _showServeSelector
    
    // Exponera game mode-tillstånd
    val gameMode: State<GameMode> = _gameMode
    val showModeSelector: State<Boolean> = _showModeSelector
    val mexicanoMatchLimit: State<Int> = _mexicanoMatchLimit

    private fun saveCurrentStateToHistory() {
        val currentState = ScoreState(
            playerOneScore = _playerOneScore.value,
            playerTwoScore = _playerTwoScore.value,
            playerOneGamesWon = _playerOneGamesWon.value,
            playerTwoGamesWon = _playerTwoGamesWon.value,
            playerOneSetsWon = _playerOneSetsWon.value,
            playerTwoSetsWon = _playerTwoSetsWon.value,
            isTieBreak = _isTieBreak.value,
            playerOneTieBreakPoints = _playerOneTieBreakPoints.value,
            playerTwoTieBreakPoints = _playerTwoTieBreakPoints.value,
            isPlayerOneServing = _isPlayerOneServing.value,
            gameMode = _gameMode.value,
            mexicanoMatchLimit = _mexicanoMatchLimit.value
        )
        _stateHistory.add(currentState)

        if (_stateHistory.size > 20) {
            _stateHistory.removeAt(0)
        }

        _canUndo.value = true
    }

    fun incrementPlayerOneScore() {
        saveCurrentStateToHistory()

        when (_gameMode.value) {
            GameMode.VINNARBANA -> {
                if (_isTieBreak.value) {
                    // I tiebreak räknas poängen annorlunda (1, 2, 3 istället för 15, 30, 40)
                    _playerOneTieBreakPoints.value += 1

                    // Uppdatera vem som servar efter varannan poäng i tiebreak
                    if ((_playerOneTieBreakPoints.value + _playerTwoTieBreakPoints.value) % 2 == 1) {
                        switchServer()
                    }

                    checkTieBreakWin(isPlayerOne = true)
                } else {
                    when (_playerOneScore.value) {
                        POINTS_INITIAL -> _playerOneScore.value = POINTS_FIRST_STEP
                        POINTS_FIRST_STEP -> _playerOneScore.value = POINTS_SECOND_STEP
                        POINTS_SECOND_STEP -> _playerOneScore.value = POINTS_THIRD_STEP
                        POINTS_THIRD_STEP -> {
                            awardGameToPlayerOne()
                        }
                    }
                }
            }
            GameMode.MEXICANO -> {
                _playerOneScore.value += 1
                checkMexicanoMatchEnd()
            }
        }
    }

    fun incrementPlayerTwoScore() {
        saveCurrentStateToHistory()

        when (_gameMode.value) {
            GameMode.VINNARBANA -> {
                if (_isTieBreak.value) {
                    // I tiebreak räknas poängen annorlunda
                    _playerTwoTieBreakPoints.value += 1

                    // Uppdatera vem som servar efter varannan poäng i tiebreak
                    if ((_playerOneTieBreakPoints.value + _playerTwoTieBreakPoints.value) % 2 == 1) {
                        switchServer()
                    }

                    checkTieBreakWin(isPlayerOne = false)
                } else {
                    when (_playerTwoScore.value) {
                        POINTS_INITIAL -> _playerTwoScore.value = POINTS_FIRST_STEP
                        POINTS_FIRST_STEP -> _playerTwoScore.value = POINTS_SECOND_STEP
                        POINTS_SECOND_STEP -> _playerTwoScore.value = POINTS_THIRD_STEP
                        POINTS_THIRD_STEP -> {
                            awardGameToPlayerTwo()
                        }
                    }
                }
            }
            GameMode.MEXICANO -> {
                _playerTwoScore.value += 1
                checkMexicanoMatchEnd()
            }
        }
    }

    private fun awardGameToPlayerOne() {
        _playerOneGamesWon.value += 1
        resetGameScores()
        checkAndProcessSetWin(isPlayerOne = true)
    }

    private fun awardGameToPlayerTwo() {
        _playerTwoGamesWon.value += 1
        resetGameScores()
        checkAndProcessSetWin(isPlayerOne = false)
    }

    private fun checkTieBreakWin(isPlayerOne: Boolean) {
        val player1Points = _playerOneTieBreakPoints.value
        val player2Points = _playerTwoTieBreakPoints.value

        if (isPlayerOne) {
            // Kontrollera om spelare 1 har vunnit tiebreak
            if (player1Points >= TIEBREAK_POINTS_TO_WIN &&
                (player1Points - player2Points) >= TIEBREAK_MIN_POINT_DIFFERENCE) {
                // Spelare 1 vinner tiebreak och därmed setet
                _playerOneSetsWon.value += 1
                _playerOneGamesWon.value = 0
                _playerTwoGamesWon.value = 0
                resetTieBreak()
            }
        } else {
            // Kontrollera om spelare 2 har vunnit tiebreak
            if (player2Points >= TIEBREAK_POINTS_TO_WIN &&
                (player2Points - player1Points) >= TIEBREAK_MIN_POINT_DIFFERENCE) {
                // Spelare 2 vinner tiebreak och därmed setet
                _playerTwoSetsWon.value += 1
                _playerOneGamesWon.value = 0
                _playerTwoGamesWon.value = 0
                resetTieBreak()
            }
        }
    }

    private fun resetTieBreak() {
        _isTieBreak.value = false
        _playerOneTieBreakPoints.value = 0
        _playerTwoTieBreakPoints.value = 0
        _showServeSelector.value = false
    }

    // Byt vem som servar
    private fun switchServer() {
        _isPlayerOneServing.value = !_isPlayerOneServing.value
    }

    // Sätt vem som servar först i tiebreak
    fun setInitialServer(isPlayerOne: Boolean) {
        _isPlayerOneServing.value = isPlayerOne
        _showServeSelector.value = false
    }
    
    // Sätt spelläge
    fun setGameMode(mode: GameMode) {
        _gameMode.value = mode
        _showModeSelector.value = false
        resetMatch()
    }
    
    // Sätt matchgräns för Mexicano
    fun setMexicanoMatchLimit(limit: Int) {
        _mexicanoMatchLimit.value = limit
    }
    
    // Kontrollera om Mexicano-match är slut
    private fun checkMexicanoMatchEnd() {
        val totalPoints = _playerOneScore.value + _playerTwoScore.value
        if (totalPoints >= _mexicanoMatchLimit.value) {
            // Match är slut - behöver inte göra något särskilt
            // Användaren kan trycka reset för ny match
        }
    }

    fun undo(): Boolean {
        if (_stateHistory.isEmpty()) {
            _canUndo.value = false
            return false
        }

        val previousState = _stateHistory.removeAt(_stateHistory.size - 1)

        _playerOneScore.value = previousState.playerOneScore
        _playerTwoScore.value = previousState.playerTwoScore
        _playerOneGamesWon.value = previousState.playerOneGamesWon
        _playerTwoGamesWon.value = previousState.playerTwoGamesWon
        _playerOneSetsWon.value = previousState.playerOneSetsWon
        _playerTwoSetsWon.value = previousState.playerTwoSetsWon
        _isTieBreak.value = previousState.isTieBreak
        _playerOneTieBreakPoints.value = previousState.playerOneTieBreakPoints
        _playerTwoTieBreakPoints.value = previousState.playerTwoTieBreakPoints
        _isPlayerOneServing.value = previousState.isPlayerOneServing
        _gameMode.value = previousState.gameMode
        _mexicanoMatchLimit.value = previousState.mexicanoMatchLimit

        _canUndo.value = _stateHistory.isNotEmpty()

        return true
    }

    fun resetGameScores() {
        _playerOneScore.value = POINTS_INITIAL
        _playerTwoScore.value = POINTS_INITIAL
    }

    fun resetMatch() {
        saveCurrentStateToHistory()

        _playerOneScore.value = POINTS_INITIAL
        _playerTwoScore.value = POINTS_INITIAL
        _playerOneGamesWon.value = 0
        _playerTwoGamesWon.value = 0
        _playerOneSetsWon.value = 0
        _playerTwoSetsWon.value = 0
        _isTieBreak.value = false
        _playerOneTieBreakPoints.value = 0
        _playerTwoTieBreakPoints.value = 0
        _isPlayerOneServing.value = true
        
        // Visa mode-väljaren igen så användaren kan välja nytt läge
        _showModeSelector.value = true

        _stateHistory.clear()
        _canUndo.value = false
    }

    private fun checkAndProcessSetWin(isPlayerOne: Boolean) {
        // Kontrollera om vi ska starta tiebreak (6-6)
        if (_playerOneGamesWon.value == 6 && _playerTwoGamesWon.value == 6) {
            _isTieBreak.value = true
            _showServeSelector.value = true
            return
        }

        if (isPlayerOne) {
            if (_playerOneGamesWon.value >= GAMES_TO_WIN_SET &&
                _playerOneGamesWon.value - _playerTwoGamesWon.value >= MIN_GAME_DIFFERENCE) {

                _playerOneSetsWon.value += 1
                _playerOneGamesWon.value = 0
                _playerTwoGamesWon.value = 0
            }
        } else {
            if (_playerTwoGamesWon.value >= GAMES_TO_WIN_SET &&
                _playerTwoGamesWon.value - _playerOneGamesWon.value >= MIN_GAME_DIFFERENCE) {

                _playerTwoSetsWon.value += 1
                _playerOneGamesWon.value = 0
                _playerTwoGamesWon.value = 0
            }
        }
    }
}