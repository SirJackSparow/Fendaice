# Data Layer — Android

Create files in:
`composeApp/src/androidMain/kotlin/com/dg/fendaice/mathgame/data/`

---

## File 1: MathQuestion.kt

Data class mapping to a Firestore document:

```kotlin
data class MathQuestion(
    val id: String = "",
    val questionText: String = "",
    val answerTiles: List<String> = emptyList(),
    val correctAnswer: String = "",
    val topic: String = "",
    val ageGroup: String = "",
    val level: Int = 1
)
```

---

## File 2: MathQuestionRepository.kt

Fetches questions from Firestore. Rules:
- Use `FirebaseFirestore.getInstance()`
- Query `math_questions` collection filtered by `ageGroup`, `topic`, `level`
- Limit to 10 documents per round
- Return as `Flow<List<MathQuestion>>`
- Use `.snapshots().map { }` to convert to flow
- Handle empty results — if no questions found for the filter,
  fall back to `ageGroup` only (drop topic filter)
- Handle errors — emit empty list and log the error, do not crash

```kotlin
class MathQuestionRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getQuestions(
        ageGroup: String,
        topic: String,
        level: Int
    ): Flow<List<MathQuestion>> =
        db.collection("math_questions")
            .whereEqualTo("ageGroup", ageGroup)
            .whereEqualTo("topic", topic)
            .whereEqualTo("level", level)
            .limit(10)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.toObject(MathQuestion::class.java)?.copy(id = doc.id)
                        ?: MathQuestion()
                }
            }
            .catch { e ->
                android.util.Log.e("MathGame", "Firestore error: ${e.message}")
                emit(emptyList())
            }
}
```