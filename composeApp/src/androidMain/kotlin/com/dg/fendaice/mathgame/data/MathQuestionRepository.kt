package com.dg.fendaice.mathgame.data

import android.content.Context
import android.util.Log
import com.dg.fendaice.mathgame.data.local.QuestionDatabase
import com.dg.fendaice.mathgame.data.local.toDomain
import com.dg.fendaice.mathgame.data.local.toEntity
import com.dg.fendaice.mathgame.data.sync.SyncManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class MathQuestionRepository(context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val questionDao = QuestionDatabase.getDatabase(context).questionDao()
    private val syncManager = SyncManager(context)

    fun getQuestions(
        ageGroup: String,
        topic: String,
        level: Int
    ): Flow<List<MathQuestion>> = flow {
        // 1. Emit current local data (if any)
        val localQuestions = questionDao.getQuestions(ageGroup, topic, level).first()
        if (localQuestions.isNotEmpty()) {
            emit(localQuestions.map { it.toDomain() })
        }

        // 2. Check if DB is totally empty or sync interval passed
        val totalCount = questionDao.getCount()
        val shouldSync = syncManager.shouldSync()

        if (totalCount == 0 || shouldSync) {
            try {
                val snapshot = db.collection("math_questions").get().await()
                val remoteQuestions = snapshot.documents.map { doc ->
                    doc.toObject(MathQuestion::class.java)?.copy(id = doc.id) ?: MathQuestion()
                }

                if (remoteQuestions.isNotEmpty()) {
                    questionDao.insertQuestions(remoteQuestions.map { it.toEntity() })
                    syncManager.updateSyncTime()
                    
                    // 3. Emit updated data if we didn't emit before or if it changed
                    val updatedLocal = questionDao.getQuestions(ageGroup, topic, level).first()
                    emit(updatedLocal.map { it.toDomain() })
                }
            } catch (e: Exception) {
                Log.e("MathGame", "Sync failed: ${e.message}")
            }
        }
    }
}
