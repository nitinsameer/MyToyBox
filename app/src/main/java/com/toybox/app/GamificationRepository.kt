package com.toybox.app

import android.content.Context
import kotlinx.coroutines.flow.Flow

class GamificationRepository(context: Context) {

    private val prefs = GamificationPreferences(context)

    val state: Flow<GamificationState> = prefs.state

    /**
     * Call after a toy is added. Returns a newly earned [Badge], or null if none earned.
     * [newToyCount] = toy count AFTER the addition.
     */
    suspend fun onToyAdded(newToyCount: Int, currentState: GamificationState): Badge? {
        prefs.addPoints(10)
        val newBadge = evaluateBadge(newToyCount, currentState.earnedBadges)
        if (newBadge != null) prefs.addBadge(newBadge)
        return newBadge
    }

    /** Add arbitrary points (used by challenge rewards + surprise rewards). */
    suspend fun addPoints(delta: Int) = prefs.addPoints(delta)

    /** GDPR Art. 17 — clears points and badges. */
    suspend fun clearAll() = prefs.clearAll()

    private fun evaluateBadge(toyCount: Int, earned: Set<Badge>): Badge? = when {
        toyCount >= 10 && Badge.TEN_TOYS  !in earned -> Badge.TEN_TOYS
        toyCount >= 5  && Badge.FIVE_TOYS !in earned -> Badge.FIVE_TOYS
        toyCount >= 1  && Badge.FIRST_TOY !in earned -> Badge.FIRST_TOY
        else -> null
    }
}
