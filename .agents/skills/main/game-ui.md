# Compose UI — Android

Create files in:
`composeApp/src/androidMain/kotlin/com/dg/fendaice/mathgame/ui/`

---

## Screens

### MenuScreen.kt
Grid of category cards with icons and labels.
On tap → calls `viewModel.startGame` and navigates to `GameScreen`.

### GameScreen.kt — main question screen

Layout:
```
┌──────────────────────────────┐
│  Score: 30     Q: 3/10      │
│                              │
│    3  +  [ ? ]  =  7        │  ← question area
│                              │
│      [ 2 ]    [ 4 ]         │  ← clickable tiles
│      [ 5 ]    [ 7 ]         │
└──────────────────────────────┘
```

### Interaction implementation

Use a `clickable` pattern for answer selection:

```kotlin
// Answer Tile
Box(
    modifier = Modifier
        .size(100.dp, 70.dp)
        .shadow(4.dp, RoundedCornerShape(20.dp))
        .background(Color.White, RoundedCornerShape(20.dp))
        .clip(RoundedCornerShape(20.dp))
        .clickable { viewModel.submitAnswer(tileText) },
    contentAlignment = Alignment.Center
) {
    Text(tileText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
}
```

### Correct answer animation
When `lastAnswerCorrect == true`:
- Question box flashes green using `animateColorAsState`
- Shows the correct number inside the box instead of "?"
- Scale bounces using `animateFloatAsState` with spring

### Wrong answer animation
When `lastAnswerCorrect == false`:
- Question box flashes red using `animateColorAsState`
- Box shakes or vibrates (optional)

### ScoreScreen.kt
- Animated stars based on performance
- "Try Again" and "Exit" buttons

### LoadingScreen.kt
Show `CircularProgressIndicator` while Firestore/Room is loading.
