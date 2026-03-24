package com.toybox.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = UserRepository(app)

    /**
     * null  → DataStore not yet read (loading)
     * ""    → DataStore read, key absent (first launch / app reinstall)
     * "…"   → name is saved
     */
    val kidName: StateFlow<String?> = repository.kidName
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.Eagerly,
            initialValue = null
        )

    /** True when a parent has explicitly accepted the privacy/consent notice. */
    val parentalConsent: StateFlow<Boolean> = repository.parentalConsent
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.Eagerly,
            initialValue = false
        )

    fun saveKidName(name: String) {
        viewModelScope.launch { repository.saveKidName(name) }
    }

    fun saveParentalConsent(granted: Boolean) {
        viewModelScope.launch { repository.saveParentalConsent(granted) }
    }

    /** GDPR Art. 17 — clears only the user DataStore (name + consent). */
    fun clearUserData() {
        viewModelScope.launch { repository.clearAll() }
    }
}
