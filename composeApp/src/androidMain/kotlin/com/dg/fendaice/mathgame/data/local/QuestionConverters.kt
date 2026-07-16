package com.dg.fendaice.mathgame.data.local

import androidx.room.TypeConverter

class QuestionConverters {
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toList(data: String): List<String> = data.split(",")
}
