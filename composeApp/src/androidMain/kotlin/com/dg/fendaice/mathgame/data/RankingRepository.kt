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

    suspend fun pushStatsToFirestore(stats: UserStats, force: Boolean = false) {
        // If score is 0 or force is true, we push immediately
        // otherwise we check the 24h sync rule
        if (!force && stats.totalScore != 0 && !syncManager.shouldPushToFirestore()) return

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
        }
    }

    suspend fun fetchGlobalRankings(force: Boolean = false) {
        if (!force && !syncManager.shouldRefreshRankings()) return
        
        if (force && syncManager.getRemainingManualRefreshes() <= 0) return

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
        }
    }
}
