# Firestore Setup — Android

Before writing any code, verify all of the following. Stop and tell the
user if anything is missing.

## 1. google-services.json

Must exist at `composeApp/google-services.json`.
If missing → tell the user to download it from Firebase Console →
Project settings → Your apps → Android app.

## 2. Firestore enabled

Firebase Console → Firestore Database → must show "Active".
If not → tell user to create a database (Start in test mode for development).

## 3. google-services plugin applied

`composeApp/build.gradle.kts` must have:
```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

Root `build.gradle.kts` must have:
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

## 4. Firestore security rules (development)

In Firebase Console → Firestore → Rules, set:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read: if true;
      allow write: if false;
    }
  }
}
```
This allows anyone to read questions but not write. Update rules before
going to production.