package com.dg.fendaice.mathgame.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingDao {
    @Query("SELECT * FROM ranking_users ORDER BY rank ASC")
    fun getAllRankings(): Flow<List<RankingUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRankings(rankings: List<RankingUser>)

    @Query("DELETE FROM ranking_users")
    suspend fun clearRankings()
}
