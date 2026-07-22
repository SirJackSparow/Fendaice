---
name: Secure Password Encryption and Registration
description: Manages secure user registration using SHA-256 hashing to protect user passwords in both local storage and Firebase Firestore.
---

# Secure Password Encryption and Registration

This skill implements a security-first approach to user registration by ensuring that plain-text passwords never touch the database.

## Security Architecture

- **One-Way Hashing**: Passwords are encrypted using the SHA-256 algorithm before storage.
- **Admin Privacy**: Since only the hash is stored, administrators and developers cannot recover or see the original plain-text passwords.
- **Local & Cloud Sync**: The hashed password is stored in the local Room database (`UserStats` table) and synchronized with the Firebase Firestore `users` collection.

## Implementation Details

### Hashing Utility
A custom extension function `String.toSha256()` in `HashUtils.kt` handles the conversion:
```kotlin
fun String.toSha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    // ... logic to return hex string
}
```

### Registration Flow
1. **User Input**: User enters Name and Password in the `RegistrationScreen`.
2. **Obfuscation**: The UI uses `PasswordVisualTransformation` to mask input.
3. **Encryption**: The `MathGameViewModel` hashes the password immediately upon registration.
4. **Persistence**: The resulting `UserStats` object containing the `passwordHash` is saved to Room and Firestore.

## Best Practices
- Always use the `passwordHash` field for authentication checks.
- Never log the plain-text password in logcat or analytics.
