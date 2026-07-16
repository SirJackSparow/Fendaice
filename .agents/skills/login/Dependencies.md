# Login Dependencies

Add these to your project files to enable Firebase and Google Sign-In.

## 1. `gradle/libs.versions.toml`

```toml
[versions]
firebaseBom = "33.9.0"
credentials = "1.3.0"
googleid = "1.1.1"
googleServices = "4.4.2"

[libraries]
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth-ktx" }
androidx-credentials = { group = "androidx.credentials", name = "credentials", version.ref = "credentials" }
androidx-credentials-play-services-auth = { group = "androidx.credentials", name = "credentials-play-services-auth", version.ref = "credentials" }
googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }

[plugins]
google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
```

## 2. Root `build.gradle.kts`

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

## 3. `composeApp/build.gradle.kts`

```kotlin
plugins {
    id("com.google.gms.google-services")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.auth)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)
        }
    }
}
```
