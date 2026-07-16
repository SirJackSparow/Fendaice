package com.dg.fendaice.mathgame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dg.fendaice.mathgame.data.MathQuestion
import com.dg.fendaice.mathgame.data.MathQuestionRepository
import com.dg.fendaice.mathgame.data.local.QuestionDatabase
import com.dg.fendaice.mathgame.data.local.UserStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MathGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MathQuestionRepository(application)
    private val userDao = QuestionDatabase.getDatabase(application).userDao()

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

    fun registerUser(name: String) {
        viewModelScope.launch {
            userDao.updateStats(UserStats(userName = name))
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
        viewModelScope.launch {
            if (correct) {
                _score.value += 10
                _lastAnswerCorrect.value = true
            } else {
                _score.value = maxOf(0, _score.value - 5)
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
            val current = userStats.value ?: UserStats()
            userDao.updateStats(
                current.copy(
                    totalScore = current.totalScore + _score.value,
                    gamesPlayed = current.gamesPlayed + 1
                )
            )
        }
    }

    fun restartGame() {
        currentParams?.let { (ageGroup, topic, level) ->
            startGame(ageGroup, topic, level)
        }
    }
}
