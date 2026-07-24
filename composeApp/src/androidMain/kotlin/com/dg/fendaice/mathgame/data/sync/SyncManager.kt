package com.dg.fendaice.mathgame.data.sync

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sync_prefs")

class SyncManager(private val context: Context) {
    private val LAST_SYNC_KEY = longPreferencesKey("last_sync_time")
    private val LAST_FIRESTORE_PUSH_KEY = longPreferencesKey("last_firestore_push_time")
    private val LAST_RANKING_FETCH_KEY = longPreferencesKey("last_ranking_fetch_time")
    private val LAST_MANUAL_REFRESH_TIME_KEY = longPreferencesKey("last_manual_refresh_time")
    private val MANUAL_REFRESH_COUNT_KEY = androidx.datastore.preferences.core.intPreferencesKey("manual_refresh_count")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val SAVED_USER_NAME_KEY = stringPreferencesKey("saved_user_name")
    private val SAVED_PASSWORD_HASH_KEY = stringPreferencesKey("saved_password_hash")

    suspend fun getUserId(): String {
        val prefs = context.dataStore.data.first()
        var userId = prefs[USER_ID_KEY]
        if (userId == null) {
            userId = generateDeviceId()
            context.dataStore.edit { it[USER_ID_KEY] = userId }
        }
        return userId
    }

    suspend fun setUserId(id: String) {
        context.dataStore.edit { it[USER_ID_KEY] = id }
    }

    suspend fun resetUserId() {
        context.dataStore.edit { it[USER_ID_KEY] = generateDeviceId() }
    }

    private fun generateDeviceId(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..8)
            .map { allowedChars.random() }
            .joinToString("")
    }

    suspend fun saveCredentials(userName: String, passwordHash: String) {
        context.dataStore.edit { prefs ->
            prefs[SAVED_USER_NAME_KEY] = userName
            prefs[SAVED_PASSWORD_HASH_KEY] = passwordHash
        }
    }

    suspend fun clearCredentials() {
        context.dataStore.edit { prefs ->
            prefs.remove(SAVED_USER_NAME_KEY)
            prefs.remove(SAVED_PASSWORD_HASH_KEY)
        }
    }

    suspend fun shouldPushToFirestore(): Boolean {
        val lastPush = context.dataStore.data.map { it[LAST_FIRESTORE_PUSH_KEY] ?: 0L }.first()
        val oneDayMillis = 24L * 60 * 60 * 1000
        return System.currentTimeMillis() - lastPush > oneDayMillis
    }

    suspend fun updateFirestorePushTime() {
        context.dataStore.edit { prefs ->
            prefs[LAST_FIRESTORE_PUSH_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun shouldRefreshRankings(): Boolean {
        val lastFetch = context.dataStore.data.map { it[LAST_RANKING_FETCH_KEY] ?: 0L }.first()
        val twoDaysMillis = 2L * 24 * 60 * 60 * 1000
        return System.currentTimeMillis() - lastFetch > twoDaysMillis
    }

    suspend fun getRemainingManualRefreshes(): Int {
        val prefs = context.dataStore.data.first()
        val lastRefreshTime = prefs[LAST_MANUAL_REFRESH_TIME_KEY] ?: 0L

        if (isDifferentDay(lastRefreshTime, System.currentTimeMillis())) {
            resetManualRefreshCount()
            return 6
        }

        val count = prefs[MANUAL_REFRESH_COUNT_KEY] ?: 0
        return maxOf(0, 6 - count)
    }

    private fun isDifferentDay(time1: Long, time2: Long): Boolean {
        if (time1 == 0L) return true
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(java.util.Calendar.YEAR) != cal2.get(java.util.Calendar.YEAR) ||
               cal1.get(java.util.Calendar.DAY_OF_YEAR) != cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    suspend fun incrementManualRefreshCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[MANUAL_REFRESH_COUNT_KEY] ?: 0
            prefs[MANUAL_REFRESH_COUNT_KEY] = current + 1
            prefs[LAST_MANUAL_REFRESH_TIME_KEY] = System.currentTimeMillis()
        }
    }

    private suspend fun resetManualRefreshCount() {
        context.dataStore.edit { prefs ->
            prefs[MANUAL_REFRESH_COUNT_KEY] = 0
        }
    }

    suspend fun updateRankingFetchTime() {
        context.dataStore.edit { prefs ->
            prefs[LAST_RANKING_FETCH_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun shouldSync(): Boolean {
        val lastSync = context.dataStore.data.map { it[LAST_SYNC_KEY] ?: 0L }.first()
        val twoWeeksMillis = 14L * 24 * 60 * 60 * 1000
        return System.currentTimeMillis() - lastSync > twoWeeksMillis
    }

    suspend fun updateSyncTime() {
        context.dataStore.edit { prefs ->
            prefs[LAST_SYNC_KEY] = System.currentTimeMillis()
        }
    }
}
