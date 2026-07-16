package com.dg.fendaice.mathgame.data.sync

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sync_prefs")

class SyncManager(private val context: Context) {
    private val LAST_SYNC_KEY = longPreferencesKey("last_sync_time")

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
