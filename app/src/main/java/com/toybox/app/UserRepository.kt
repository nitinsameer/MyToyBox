package com.toybox.app

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserRepository(context: Context) {

    private val prefs = UserPreferences(context)

    val kidName: Flow<String>         = prefs.kidName
    val parentalConsent: Flow<Boolean> = prefs.parentalConsent

    suspend fun saveKidName(name: String) {
        prefs.setKidName(name.trim())
    }

    suspend fun saveParentalConsent(granted: Boolean) {
        prefs.setParentalConsent(granted)
    }

    /** Erases child's name and consent record from DataStore. */
    suspend fun clearAll() {
        prefs.clearAll()
    }
}
