package com.dg.fendaice.mathgame.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MathQuestion(
    val id: String = "",
    val questionText: String = "",
    val answerTiles: List<String> = emptyList(),
    val correctAnswer: String = "",
    val topic: String = "",
    val ageGroup: String = "",
    val level: Int = 1
)