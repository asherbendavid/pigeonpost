package cvc.dashingdog.pigeonpost.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "pigeonpost_prefs")

class FeedStore(private val context: Context) {

    private val gson = Gson()
    private val key = stringPreferencesKey("saved_feed_json")

    suspend fun loadSavedItems(): List<FeedItem> {
        return try {
            val json = context.dataStore.data.first()[key] ?: return emptyList()
            val type = object : TypeToken<List<FeedItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            // Corrupt or unreadable — treat as empty, don't crash
            emptyList()
        }
    }

    suspend fun saveItems(items: List<FeedItem>) {
        try {
            val json = gson.toJson(items)
            context.dataStore.edit { prefs -> prefs[key] = json }
        } catch (e: Exception) {
            // Fail silently per the "not high stakes" spec — next successful
            // poll will just try saving again
        }
    }
}