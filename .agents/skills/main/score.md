# Scoring and Progression — Android

## Score per question
- Correct first try → +10 points
- Wrong (tile bounces back, try again) → +0 points

## Star rating per round
- 90–100 points → 3 stars ⭐⭐⭐
- 60–89 points → 2 stars ⭐⭐
- 30–59 points → 1 star ⭐
- Below 30 → 0 stars, must retry to unlock next level

## Level unlock rule
- 2+ stars → next level unlocks
- Level 1 → 2 → 3 → 4 → 5 per topic

## Save progress with DataStore

Use `androidx.datastore` to persist level and stars:

```kotlin
// In androidMain
val Context.dataStore by preferencesDataStore(name = "math_progress")

suspend fun saveProgress(context: Context, topic: String, level: Int, stars: Int) {
    val key = stringPreferencesKey("${topic}_level${level}_stars")
    context.dataStore.edit { prefs -> prefs[key] = stars.toString() }
}

fun getProgress(context: Context, topic: String, level: Int): Flow<Int> {
    val key = stringPreferencesKey("${topic}_level${level}_stars")
    return context.dataStore.data.map { prefs -> prefs[key]?.toInt() ?: 0 }
}
```

Add dependency:
```kotlin
implementation("androidx.datastore:datastore-preferences:1.1.1")
```

## Streak bonus
3 correct in a row → show "🔥 On Fire!" toast + 5 bonus points.
Track with `consecutiveCorrect: Int` in ViewModel.
Reset to 0 on wrong answer.