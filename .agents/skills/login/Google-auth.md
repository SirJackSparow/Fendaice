# Google Authentication Logic

This document details the implementation of the authentication flow using Credential Manager and Firebase.

## 1. Login UI State
Tracks the status of the sign-in process.

```kotlin
data class LoginUiState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
```

## 2. Google Auth Client
Handles the interaction with `Credential Manager` and signs into `Firebase`.

```kotlin
class GoogleAuthClient(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val credentialManager = CredentialManager.create(context)
    // Get this from google-services.json (client_type 3)
    private val webClientId = "YOUR_WEB_CLIENT_ID" 

    suspend fun signIn(): Result<Boolean> {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        return handleSignInResult(result)
    }

    private suspend fun handleSignInResult(result: GetCredentialResponse): Result<Boolean> {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
            firebaseAuth.signInWithCredential(firebaseCredential).await()
            return Result.success(true)
        }
        return Result.failure(Exception("Invalid credential type"))
    }
}
```

## 3. Login ViewModel
Manages the UI state and communicates with the UI.

```kotlin
class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onSignInResult(isSuccess: Boolean, errorMessage: String?) {
        _state.update { it.copy(
            isSignInSuccessful = isSuccess,
            signInError = errorMessage
        ) }
    }
}
```
