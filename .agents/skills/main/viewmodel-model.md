# ViewModel — Android

Create file:
`composeApp/src/androidMain/kotlin/com/dg/fendaice/mathgame/MathGameViewModel.kt`

## State to expose

```kotlin
// Current question being shown
val currentQuestion: StateFlow<MathQuestion?>

// All 10 questions for this round
val questions: StateFlow<List<MathQuestion>>

// Current question index (0-9)
val currentIndex: StateFlow<Int>

// Score this round
val score: StateFlow<Int>

// Whether the round is complete
val isComplete: StateFlow<Boolean>

// Loading state while Firestore fetches
val isLoading: StateFlow<Boolean>

// Error message if Firestore fails
val error: StateFlow<String?>

// Whether last answer was correct (for animation trigger)
val lastAnswerCorrect: StateFlow<Boolean?>
```

## Functions to implement

### `fun startGame(ageGroup: String, topic: String, level: Int)`
- Set `isLoading = true`
- Collect from `MathQuestionRepository.getQuestions()`
- On success → shuffle the list, set `questions`, set `currentIndex = 0`,
  set `isLoading = false`
- On empty → set `error = "No questions found for this level"`

### `fun submitAnswer(answer: String)`
- Compare `answer` with `currentQuestion.correctAnswer`
- If correct → `score += 10`, set `lastAnswerCorrect = true`,
  wait 800ms, call `nextQuestion()`
- If wrong → set `lastAnswerCorrect = false`,
  wait 500ms, reset `lastAnswerCorrect = null` (tile bounces back)

### `fun nextQuestion()`
- Increment `currentIndex`
- If `currentIndex >= questions.size` → set `isComplete = true`
- Otherwise → update `currentQuestion`

### `fun restartGame()`
- Reset all state to initial values
- Re-fetch questions with same parameters