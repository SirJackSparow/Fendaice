# Wiring Auth into the App

To make the login screen effective, it must be the first thing a user sees if they are not authenticated.

## MainActivity Implementation

In your `MainActivity.kt`, wrap your content with a check for the current user.

```kotlin
class MainActivity : ComponentActivity() {
    private val googleAuthClient by lazy { GoogleAuthClient(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<LoginViewModel>()
            val state by viewModel.state.collectAsState()

            // 1. Check if user is already signed in
            val user = googleAuthClient.getSignedInUser()

            if (user != null || state.isSignInSuccessful) {
                // 2. Show the Main App
                App()
            } else {
                // 3. Show the Login Screen
                LoginScreen(
                    state = state,
                    onSignInClick = {
                        lifecycleScope.launch {
                            val result = googleAuthClient.signIn()
                            viewModel.onSignInResult(
                                isSuccess = result.isSuccess,
                                errorMessage = result.exceptionOrNull()?.message
                            )
                        }
                    }
                )
            }
        }
    }
}
```

## Navigation Tips
For larger apps, consider using **Jetpack Compose Navigation** to handle this logic within a `NavHost` by redirecting to a `login` route if `auth.currentUser == null`.
