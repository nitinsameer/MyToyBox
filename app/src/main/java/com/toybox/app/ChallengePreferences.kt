package com.toybox.app

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

val Context.challengeDataStore by preferencesDataStore(name = "challenge_prefs")

enum class ChallengeType { DAILY, ONGOING }

@Immutable
data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val targetCount: Int,
    val type: ChallengeType,
    val rewardPoints: Int,
    val targetCategory: String? = null   // null = any category
)

@Immutable
data class ChallengeProgress(
    val challenge: Challenge,
    val currentCount: Int,
    val isCompleted: Boolean
) {
    val progressFraction: Float
        get() = (currentCount.toFloat() / challenge.targetCount).coerceIn(0f, 1f)
}

val CHALLENGE_DEFINITIONS = listOf(
    Challenge(
        id = "daily_add_1",
        title = "Toy of the Day!",
        description = "Add 1 toy today",
        emoji = "🌟",
        targetCount = 1,
        type = ChallengeType.DAILY,
        rewardPoints = 20
    ),
    Challenge(
        id = "ongoing_add_3",
        title = "Collector Goal",
        description = "Add 3 toys total",
        emoji = "🎯",
        targetCount = 3,
        type = ChallengeType.ONGOING,
        rewardPoints = 30
    ),
    Challenge(
        id = "puzzle_master",
        title = "Puzzle Master!",
        description = "Add a Puzzle toy",
        emoji = "🧩",
        targetCount = 1,
        type = ChallengeType.ONGOING,
        rewardPoints = 25,
        targetCategory = "Puzzle"
    )
)

class ChallengePreferences(private val context: Context) {

    companion object {
        private val LAST_DATE_KEY = stringPreferencesKey("challenge_last_date")
        private fun countKey(id: String) = intPreferencesKey("ch_count_$id")
        private fun doneKey(id: String)  = booleanPreferencesKey("ch_done_$id")
    }

    val progresses: Flow<List<ChallengeProgress>> = context.challengeDataStore.data.map { prefs ->
        val today     = todayString()
        val lastDate  = prefs[LAST_DATE_KEY] ?: ""
        val dailyReset = lastDate != today

        CHALLENGE_DEFINITIONS.map { ch ->
            val count = if (ch.type == ChallengeType.DAILY && dailyReset) 0
                        else prefs[countKey(ch.id)] ?: 0
            val done  = if (ch.type == ChallengeType.DAILY && dailyReset) false
                        else prefs[doneKey(ch.id)] ?: false
            ChallengeProgress(ch, count, done)
        }
    }

    suspend fun incrementAndStampDate(id: String, newCount: Int) {
        context.challengeDataStore.edit { prefs ->
            prefs[LAST_DATE_KEY] = todayString()
            prefs[countKey(id)]  = newCount
        }
    }

    suspend fun markCompleted(id: String) {
        context.challengeDataStore.edit { prefs ->
            prefs[doneKey(id)] = true
        }
    }

    /** GDPR Art. 17 — wipes all challenge progress. */
    suspend fun clearAll() {
        context.challengeDataStore.edit { it.clear() }
    }

    private fun todayString() =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
