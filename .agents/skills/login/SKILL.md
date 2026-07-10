---
name: login-jetpack-compose
description: >
  Adds a complete Google Sign-In login screen to the Android target of a
  Kotlin Multiplatform Mobile (KMM) project using Jetpack Compose and Firebase
  Authentication. Use this skill when the user asks to create, add, build, or
  implement a login page, sign-in screen, authentication screen, or Google
  login flow in the composeApp module. This skill uses the modern Credential
  Manager API (not the deprecated GoogleSignInClient), MVVM architecture with
  ViewModel and StateFlow, and Jetpack Compose UI.
---

## Goal

Create a complete Google Sign-In login screen inside the `composeApp` module
of this KMM project. Follow each step in order. Each step references a
dedicated file for the full technical detail.

---

## Steps 

### Step 1: Verify prerequisites
Check Firebase setup, SHA-1 fingerprint, and google-services.json placement.
See [references/prerequisites.md](references/prerequisites.md).

### Step 2: Add dependencies
Add Firebase and Credential Manager dependencies to `composeApp/build.gradle.kts`.
See [references/dependencies.md](references/dependencies.md).

### Step 3: Implement Firebase + Google auth logic
Create `GoogleAuthClient.kt`, `LoginViewModel.kt`, and `LoginUiState.kt`.
See [references/google-auth.md](references/google-auth.md).

### Step 4: Implement the Compose UI
Create `LoginScreen.kt` with the Google Sign-In button and state handling.
See [references/login-screen.md](references/login-screen.md).

### Step 5: Wire into navigation
Add the login route to the app's `NavHost`.
See [references/navigation.md](references/navigation.md).

### Step 6: Validate and troubleshoot
Build and run the app. If sign-in fails, check common causes.
See [references/troubleshooting.md](references/troubleshooting.md).