package com.toybox.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KID_NAME         = stringPreferencesKey("kid_name")
        /** True only after a parent explicitly ticked the consent checkbox on SetupScreen. */
        val PARENTAL_CONSENT = booleanPreferencesKey("parental_consent")
    }

    // "" = key absent (first launch), non-empty = name saved
    val kidName: Flow<String> = context.userDataStore.data.map { prefs ->
        prefs[KID_NAME] ?: ""
    }

    val parentalConsent: Flow<Boolean> = context.userDataStore.data.map { prefs ->
        prefs[PARENTAL_CONSENT] ?: false
    }

    suspend fun setKidName(name: String) {
        context.userDataStore.edit { prefs ->
            prefs[KID_NAME] = name
        }
    }

    suspend fun setParentalConsent(granted: Boolean) {
        context.userDataStore.edit { prefs ->
            prefs[PARENTAL_CONSENT] = granted
        }
    }

    /** GDPR Art. 17 — Right to Erasure: wipe every key in this store. */
    suspend fun clearAll() {
        context.userDataStore.edit { it.clear() }
    }
}
