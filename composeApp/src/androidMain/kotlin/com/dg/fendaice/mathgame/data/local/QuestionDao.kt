package com.dg.fendaice.mathgame.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE ageGroup = :ageGroup AND topic = :topic AND level = :level")
    fun getQuestions(ageGroup: String, topic: String, level: Int): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getCount(): Int
}
