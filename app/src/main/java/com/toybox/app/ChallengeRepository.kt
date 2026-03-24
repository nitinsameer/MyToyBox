package com.toybox.app

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(context: Context) {

    private val prefs = ChallengePreferences(context)

    val challenges: Flow<List<ChallengeProgress>> = prefs.progresses

    /** GDPR Art. 17 — clears all challenge progress. */
    suspend fun clearAll() = prefs.clearAll()

    /**
     * Evaluate all active challenges against the toy just added.
     * Returns list of newly-completed [ChallengeProgress].
     */
    suspend fun onToyAdded(
        toy: Toy,
        current: List<ChallengeProgress>
    ): List<ChallengeProgress> {
        val completed = mutableListOf<ChallengeProgress>()

        current.filter { !it.isCompleted }.forEach { progress ->
            val ch = progress.challenge
            val qualifies = ch.targetCategory == null ||
                toy.category.equals(ch.targetCategory, ignoreCase = true)

            if (qualifies) {
                val newCount = progress.currentCount + 1
                prefs.incrementAndStampDate(ch.id, newCount)

                if (newCount >= ch.targetCount) {
                    prefs.markCompleted(ch.id)
                    completed.add(ChallengeProgress(ch, newCount, isCompleted = true))
                }
            }
        }

        return completed
    }
}
