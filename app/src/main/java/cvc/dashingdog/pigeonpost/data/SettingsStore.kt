package cvc.dashingdog.pigeonpost.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.settingsDataStore by preferencesDataStore(name = "pigeonpost_settings")

class SettingsStore(private val context: Context) {

    private val intervalKey = longPreferencesKey("poll_interval_hours")
    private val filterKey = stringPreferencesKey("exclude_keywords")

    companion object {
        const val DEFAULT_INTERVAL_HOURS = 48L
        const val DEFAULT_EXCLUDE_KEYWORDS = "Canary"
    }

    suspend fun getIntervalHours(): Long {
        return try {
            context.settingsDataStore.data.first()[intervalKey] ?: DEFAULT_INTERVAL_HOURS
        } catch (_: Exception) {
            DEFAULT_INTERVAL_HOURS
        }
    }

    suspend fun setIntervalHours(hours: Long) {
        try {
            context.settingsDataStore.edit { it[intervalKey] = hours }
        } catch (_: Exception) { /* fail silently, non-critical */ }
    }

    /** Comma-separated keywords; a title is excluded if it contains any of them. */
    suspend fun getExcludeKeywords(): List<String> {
        return try {
            val raw = context.settingsDataStore.data.first()[filterKey] ?: DEFAULT_EXCLUDE_KEYWORDS
            raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } catch (_: Exception) {
            listOf(DEFAULT_EXCLUDE_KEYWORDS)
        }
    }

    suspend fun setExcludeKeywords(keywords: List<String>) {
        try {
            val raw = keywords.joinToString(",")
            context.settingsDataStore.edit { it[filterKey] = raw }
        } catch (_: Exception) { /* fail silently */ }
    }
}