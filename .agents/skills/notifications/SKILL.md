---
name: Firebase Cloud Messaging Setup and Notification Design
description: Manages the integration of Firebase Cloud Messaging for push notifications, including custom notification design and Android 13+ permission handling.
---

# Firebase Cloud Messaging (FCM) Setup and Notification Design

This skill manages the integration of push notifications using Firebase Cloud Messaging (FCM) with a focus on creating well-designed, user-friendly notifications.

## Implementation Overview

- **FCM Dependency**: Adds `firebase-messaging-ktx` dependency to handle cloud messaging.
- **Custom Service**: Implements a `FirebaseMessagingService` subclass to intercept and customize notifications.
- **Modern Permissions**: Handles Android 13+ notification permission requests using the `POST_NOTIFICATIONS` runtime permission.
- **Notification Design**: Uses `NotificationCompat.Builder` with custom styles (BigTextStyle, LargeIcon, etc.) to create visually appealing notifications.

## Key Components

1. **FendaiceMessagingService.kt** (`com.dg.fendaice.service`): Custom service that overrides `onMessageReceived` / `onNewToken`.
2. **Notification Channels**:
   - `fendaice_general` — general tips/updates (HIGH)
   - `fendaice_ranking` — leaderboard updates (DEFAULT)
   - `fendaice_challenge` — practice/challenge reminders (HIGH)
3. **Permission Request**: `MainActivity` requests `POST_NOTIFICATIONS` on API 33+ at launch.
4. **Manifest**:
   - `POST_NOTIFICATIONS` permission
   - Service registered with `com.google.firebase.MESSAGING_EVENT`

## Design Guidelines

- **BigTextStyle**: Longer body text expands in the shade.
- **LargeIcon**: App launcher icon.
- **SmallIcon**: `R.drawable.ic_notification` (white vector for status bar).
- **Accent color**: Brand blue (`#1E88E5`).
- **Custom Vibration**: Unique patterns for General (double short), Ranking (single long), and Challenge (rapid pulse).
- **Action Buttons**:
  - `type=ranking` → "View Ranking"
  - `type=challenge` → "Play Now"
  - default → "Open"
- **Navigation**: Deep-link support via Intent Extras (`notification_type`) to auto-navigate the user to the Ranking or Menu screen upon tap.

## Payload Convention (data messages preferred for full control)

```json
{
  "title": "Climb the ranks!",
  "body": "Your friends just passed you. Play a round.",
  "type": "ranking"
}
```

`type` values: `general` | `ranking` | `challenge`

## How to Test

1. Build/install debug app; accept notification permission on Android 13+.
2. In Firebase Console → Messaging → create campaign or send test message to device FCM token / app.
3. For custom actions/channels, send a **data** message (or notification+data) including `type`.
4. Verify shade UI: BigText, large icon, action button, correct channel.

## Files

- `composeApp/.../service/FendaiceMessagingService.kt`
- `composeApp/.../res/drawable/ic_notification.xml`
- `composeApp/src/androidMain/AndroidManifest.xml`
- `composeApp/.../MainActivity.kt` (permission)
- `gradle/libs.versions.toml` + `composeApp/build.gradle.kts` (dependency)
