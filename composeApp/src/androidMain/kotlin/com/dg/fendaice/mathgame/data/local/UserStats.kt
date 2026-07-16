package com.dg.fendaice.mathgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val userName: String = "",
    val totalScore: Int = 0,
    val gamesPlayed: Int = 0
)
