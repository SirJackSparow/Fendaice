# Prerequisites

Before writing any code, verify all of the following.
Stop and tell the user if anything is missing before proceeding.

## 1. google-services.json

Must exist at `composeApp/google-services.json`.

If missing → tell the user:
> "Please go to Firebase Console → Project settings → Your apps →
> Android app → Download google-services.json and place it inside
> the composeApp/ folder."

## 2. Google Sign-In enabled in Firebase

- Go to Firebase Console → Authentication → Sign-in providers
- Confirm Google is toggled ON
- If not → tell the user to enable it before proceeding

## 3. SHA-1 fingerprint registered

- Go to Firebase Console → Project settings → Your apps → SHA certificate fingerprints
- Confirm the debug SHA-1 is listed there

To get the debug SHA-1, run:
```
./gradlew signingReport
```
Look for `SHA1` under the `debug` variant and copy it into Firebase Console.

## 4. google-services plugin applied

Confirm `composeApp/build.gradle.kts` has:
```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

And the root `build.gradle.kts` has:
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```