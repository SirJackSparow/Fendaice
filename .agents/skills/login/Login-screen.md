# Beautiful Login Screen (Compose)

This UI provides an animated gradient background and a modern "Fendaice" themed entry.

```kotlin
@Composable
fun LoginScreen(
    state: LoginUiState,
    onSignInClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF6200EE),
        targetValue = Color(0xFF03DAC5),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color1, Color(0xFF121212)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Fendaice", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
            
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onSignInClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Text("Continue with Google", color = Color.White)
            }
        }
    }
}
```

### Key Features:
- **Animated Background**: Uses `animateColor` for a dynamic feel.
- **Glassmorphism**: The button uses a semi-transparent white container.
- **Branding**: Displays the "Fendaice" name prominently.
