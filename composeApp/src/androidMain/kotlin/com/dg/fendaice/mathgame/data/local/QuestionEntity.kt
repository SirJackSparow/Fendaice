package com.dg.fendaice.mathgame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dg.fendaice.mathgame.data.MathQuestion

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val questionText: String,
    val answerTiles: List<String>,
    val correctAnswer: String,
    val topic: String,
    val ageGroup: String,
    val level: Int
)

fun QuestionEntity.toDomain() = MathQuestion(
    id = id,
    questionText = questionText,
    answerTiles = answerTiles,
    correctAnswer = correctAnswer,
    topic = topic,
    ageGroup = ageGroup,
    level = level
)

fun MathQuestion.toEntity() = QuestionEntity(
    id = id,
    questionText = questionText,
    answerTiles = answerTiles,
    correctAnswer = correctAnswer,
    topic = topic,
    ageGroup = ageGroup,
    level = level
)
