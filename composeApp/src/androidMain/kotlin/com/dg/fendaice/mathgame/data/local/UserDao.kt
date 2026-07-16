package com.dg.fendaice.mathgame.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStats(stats: UserStats)
}
