---
name: Online Ranking and Batch Firestore Sync
description: Manages global leaderboard with Firebase Firestore and local caching. Syncs user stats to Firestore every 24 hours and refreshes global rankings every 2 days.
---

# Online Ranking and Batch Firestore Sync

This skill manages the integration between local user statistics and a global leaderboard hosted on Firebase Firestore.

## Core Features

- **Unique User Identification**: Generates and stores a unique UUID in DataStore to identify the device globally.
- **Batch Firestore Sync**: 
    - Scores are saved to local Room database immediately.
    - Statistics are pushed to Firestore only if at least 24 hours have passed since the last push.
    - This minimizes Firestore writes and handles "offline" accumulation of scores.
- **Global Leaderboard**:
    - Fetches the top 50 users globally based on their `totalScore`.
    - Rankings are cached locally in Room for performance.
    - Rankings are only refreshed from Firestore once every 48 hours (2 days) automatically.
    - **Manual Refresh Limit**: Users can manually refresh the rankings up to **2 times per day**. The count resets at midnight.
- **UI Integration**:
    - A dedicated Ranking screen displays the rank, name, and total score of top players.
    - Top 3 players are highlighted with gold, silver, and bronze icons.

## Implementation Details

### Data Sync Logic
- `SyncManager`: Manages the timestamps for Firestore pushes and ranking fetches using Jetpack DataStore.
- `RankingRepository`: Orchestrates the communication between Room and Firestore.
- `MathGameViewModel`: Triggers sync checks during score saving and screen initialization.

### Firestore Structure
- Collection: `users`
- Document ID: `userId` (UUID)
- Fields: `userId`, `userName`, `totalScore`, `gamesPlayed`, `lastUpdated`.

## How to use
When a user finishes a quiz, `saveScoreToLocal` is called. This function increments the local score and then asks `RankingRepository` to check if a Firestore push is due (24h rule). The Ranking screen automatically triggers a refresh check (2d rule) when opened or via the manual refresh button.
