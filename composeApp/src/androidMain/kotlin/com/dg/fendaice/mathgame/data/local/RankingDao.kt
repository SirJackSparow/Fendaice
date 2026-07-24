package com.dg.fendaice.mathgame.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingDao {
    @Query("SELECT * FROM ranking_users ORDER BY rank ASC, totalScore DESC")
    fun getAllRankings(): Flow<List<RankingUser>>

    @Query("SELECT * FROM ranking_users WHERE userId = :userId LIMIT 1")
    suspend fun getRankingByUserId(userId: String): RankingUser?

    @Query("SELECT * FROM ranking_users ORDER BY totalScore DESC")
    suspend fun getRankingsListOnce(): List<RankingUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRankings(rankings: List<RankingUser>)

    @Query("DELETE FROM ranking_users")
    suspend fun clearRankings()
}
