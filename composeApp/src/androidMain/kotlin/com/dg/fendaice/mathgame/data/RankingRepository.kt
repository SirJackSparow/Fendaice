package com.dg.fendaice.mathgame.data

import com.dg.fendaice.mathgame.data.local.RankingDao
import com.dg.fendaice.mathgame.data.local.RankingUser
import com.dg.fendaice.mathgame.data.local.UserStats
import com.dg.fendaice.mathgame.data.sync.SyncManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow

class RankingRepository(
    private val rankingDao: RankingDao,
    private val syncManager: SyncManager
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    val allRankings: Flow<List<RankingUser>> = rankingDao.getAllRankings()

    suspend fun loginUser(accountId: String, name: String, passwordHash: String): Boolean {
        return try {
            val doc = usersCollection.document(accountId).get().await()
            if (doc.exists()) {
                val firestoreName = doc.getString("userName") ?: ""
                val firestoreHash = doc.getString("passwordHash") ?: ""
                firestoreName == name && firestoreHash == passwordHash
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun pushStatsToFirestore(stats: UserStats, force: Boolean = false) {
        // If force is true, skip the 24h sync rule
        if (!force && !syncManager.shouldPushToFirestore()) return

        val userId = syncManager.getUserId()
        val userData = mapOf(
            "userId" to userId,
            "userName" to stats.userName,
            "passwordHash" to stats.passwordHash,
            "totalScore" to stats.totalScore,
            "gamesPlayed" to stats.gamesPlayed,
            "lastUpdated" to System.currentTimeMillis()
        )

        try {
            usersCollection.document(userId).set(userData).await()
            syncManager.updateFirestorePushTime()
        } catch (e: Exception) {
            e.printStackTrace()
            // In case of network error, ensure user's current stats are persisted locally in ranking table
            ensureCurrentUserInLocalRankings(stats)
        }
    }

    suspend fun fetchGlobalRankings(force: Boolean = false, currentUserStats: UserStats? = null) {
        if (!force && !syncManager.shouldRefreshRankings()) return

        try {
            val snapshot = usersCollection
                .orderBy("totalScore", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val rankings = snapshot.documents.mapIndexed { index, doc ->
                RankingUser(
                    userId = doc.getString("userId") ?: "",
                    userName = doc.getString("userName") ?: "Anonymous",
                    totalScore = doc.getLong("totalScore")?.toInt() ?: 0,
                    rank = index + 1
                )
            }

            if (rankings.isNotEmpty()) {
                rankingDao.clearRankings()
                rankingDao.insertRankings(rankings)
                if (force) {
                    syncManager.incrementManualRefreshCount()
                } else {
                    syncManager.updateRankingFetchTime()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If offline / network error occurs, retain local Room ranking cache
            // and ensure current user is saved in the local Room rankings
            if (currentUserStats != null && currentUserStats.userName.isNotBlank()) {
                ensureCurrentUserInLocalRankings(currentUserStats)
            }
        }
    }

    suspend fun ensureCurrentUserInLocalRankings(stats: UserStats) {
        try {
            val userId = syncManager.getUserId()
            val currentList = rankingDao.getRankingsListOnce().toMutableList()
            
            val index = currentList.indexOfFirst { it.userId == userId }
            val updatedUser = RankingUser(
                userId = userId,
                userName = stats.userName,
                totalScore = stats.totalScore,
                rank = 0
            )

            if (index >= 0) {
                currentList[index] = updatedUser
            } else {
                currentList.add(updatedUser)
            }

            // Re-sort by score and update ranks
            val sortedList = currentList
                .sortedByDescending { it.totalScore }
                .mapIndexed { i, user -> user.copy(rank = i + 1) }

            rankingDao.clearRankings()
            rankingDao.insertRankings(sortedList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
