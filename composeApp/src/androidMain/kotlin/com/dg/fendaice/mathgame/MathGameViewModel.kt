package com.dg.fendaice.mathgame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dg.fendaice.mathgame.data.MathQuestion
import com.dg.fendaice.mathgame.data.MathQuestionRepository
import com.dg.fendaice.mathgame.data.RankingRepository
import com.dg.fendaice.mathgame.data.local.QuestionDatabase
import com.dg.fendaice.mathgame.data.local.UserStats
import com.dg.fendaice.mathgame.data.sync.SyncManager
import com.dg.fendaice.mathgame.util.toSha256
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MathGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MathQuestionRepository(application)
    private val db = QuestionDatabase.getDatabase(application)
    private val userDao = db.userDao()
    private val rankingDao = db.rankingDao()
    private val syncManager = SyncManager(application)
    private val rankingRepository = RankingRepository(rankingDao, syncManager)

    private val _questions = MutableStateFlow<List<MathQuestion>>(emptyList())
    val questions = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _currentQuestion = MutableStateFlow<MathQuestion?>(null)
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score = _score.asStateFlow()

    private val _isComplete = MutableStateFlow(false)
    val isComplete = _isComplete.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _lastAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val lastAnswerCorrect = _lastAnswerCorrect.asStateFlow()

    private var currentParams: Triple<String, String, Int>? = null

    val userStats: StateFlow<UserStats?> = userDao.getUserStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val globalRankings = rankingRepository.allRankings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    private val _remainingRefreshes = MutableStateFlow(6)
    val remainingRefreshes = _remainingRefreshes.asStateFlow()

    init {
        viewModelScope.launch {
            _currentUserId.value = syncManager.getUserId()
        }
        fetchRankings()
        updateRemainingRefreshes()
    }

    private fun updateRemainingRefreshes() {
        viewModelScope.launch {
            _remainingRefreshes.value = syncManager.getRemainingManualRefreshes()
        }
    }

    fun fetchRankings(isManual: Boolean = false) {
        viewModelScope.launch {
            // Check daily refresh limit for manual refreshes
            val remaining = syncManager.getRemainingManualRefreshes()
            if (isManual && remaining <= 0) return@launch

            val currentStats = userDao.getUserStats().firstOrNull()

            if (isManual && currentStats != null) {
                // Find user in local rankings
                val localUserId = _currentUserId.value
                val userInRankings = globalRankings.value.find { it.userId == localUserId }

                // If score differs from leaderboard, force push before fetching
                if (userInRankings?.totalScore != currentStats.totalScore) {
                    rankingRepository.pushStatsToFirestore(currentStats, force = true)
                }
            }

            rankingRepository.fetchGlobalRankings(force = isManual, currentUserStats = currentStats)
            updateRemainingRefreshes()
        }
    }

    fun registerUser(name: String, password: String) {
        viewModelScope.launch {
            val passwordHash = password.toSha256()
            // Each account gets unique userId from name+password hash
            val accountId = (name + password).toSha256()
            syncManager.setUserId(accountId)
            syncManager.saveCredentials(name, passwordHash)
            _currentUserId.value = accountId

            val currentStats = userDao.getUserStats().firstOrNull() ?: UserStats()
            val updatedStats = currentStats.copy(
                userName = name,
                passwordHash = passwordHash
            )
            userDao.updateStats(updatedStats)
            // Push to Firestore so they appear in global rankings
            rankingRepository.pushStatsToFirestore(updatedStats, force = true)
            fetchRankings(isManual = true)
        }
    }

    fun loginUser(name: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val inputHash = password.toSha256()
            val accountId = (name + password).toSha256()

            val match = rankingRepository.loginUser(accountId, name, inputHash)

            if (match) {
                syncManager.setUserId(accountId)
                _currentUserId.value = accountId
                syncManager.saveCredentials(name, inputHash)

                // Restore name + hash in Room so MainActivity shows game screens
                val stats = userDao.getUserStats().firstOrNull() ?: UserStats()
                userDao.updateStats(stats.copy(userName = name, passwordHash = inputHash))

                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Don't clear DataStore credentials — needed for next login
            userDao.updateStats(UserStats())
            syncManager.resetUserId()
            _currentUserId.value = syncManager.getUserId()
        }
    }

    fun startGame(ageGroup: String, topic: String, level: Int) {
        currentParams = Triple(ageGroup, topic, level)
        _isLoading.value = true
        _error.value = null
        _isComplete.value = false
        _score.value = 0
        _currentIndex.value = 0
        
        viewModelScope.launch {
            repository.getQuestions(ageGroup, topic, level)
                .collect { fetchedQuestions ->
                    if (fetchedQuestions.isEmpty() && !_isLoading.value) {
                        _error.value = "No questions found"
                    } else if (fetchedQuestions.isNotEmpty()) {
                        val shuffled = fetchedQuestions.shuffled()
                        _questions.value = shuffled
                        _currentQuestion.value = shuffled.firstOrNull()
                        _isLoading.value = false
                    }
                }
            _isLoading.value = false
        }
    }

    fun submitAnswer(answer: String) {
        val correct = _currentQuestion.value?.correctAnswer == answer
        
        // Calculate points based on level/ageGroup
        val pointValue = when (currentParams?.first) {
            "KIDS_EASY" -> 1
            "KIDS_MEDIUM" -> 2
            "KIDS_HARD" -> 3
            "TEEN_EASY" -> 4
            "TEEN_HARD" -> 5
            "ADULT" -> 6
            else -> 1 // Default fallback
        }

        viewModelScope.launch {
            if (correct) {
                _score.value += pointValue
                _lastAnswerCorrect.value = true
            } else {
                _score.value -= pointValue
                _lastAnswerCorrect.value = false
            }
            
            delay(1000)
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        _lastAnswerCorrect.value = null
        val nextIndex = _currentIndex.value + 1
        if (nextIndex >= _questions.value.size) {
            _isComplete.value = true
            saveScoreToLocal()
        } else {
            _currentIndex.value = nextIndex
            _currentQuestion.value = _questions.value[nextIndex]
        }
    }

    private fun saveScoreToLocal() {
        viewModelScope.launch {
            // Read directly from database to avoid race conditions with Flow
            val currentStats = userDao.getUserStats().firstOrNull() ?: UserStats()
            val updatedStats = currentStats.copy(
                totalScore = currentStats.totalScore + _score.value,
                gamesPlayed = currentStats.gamesPlayed + 1
            )
            userDao.updateStats(updatedStats)
            // Always push to Firestore after game finishes
            rankingRepository.pushStatsToFirestore(updatedStats, force = true)
            // Refresh rankings to show new score
            if (syncManager.getRemainingManualRefreshes() > 0) {
                rankingRepository.fetchGlobalRankings(force = true, currentUserStats = updatedStats)
                updateRemainingRefreshes()
            }
        }
    }

    fun restartGame() {
        currentParams?.let { (ageGroup, topic, level) ->
            startGame(ageGroup, topic, level)
        }
    }
}
