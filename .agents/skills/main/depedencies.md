# Dependencies — Android

Add inside `androidMain.dependencies` in `composeApp/build.gradle.kts`:

```kotlin
androidMain.dependencies {
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Compose animation for drag-and-drop
    implementation("androidx.compose.animation:animation:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.7.8")

    // Coroutines for Firestore flows
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}
```

Sync Gradle after adding.