package com.dg.fendaice.mathgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ranking_users")
data class RankingUser(
    @PrimaryKey val userId: String,
    val userName: String,
    val totalScore: Int,
    val rank: Int
)
