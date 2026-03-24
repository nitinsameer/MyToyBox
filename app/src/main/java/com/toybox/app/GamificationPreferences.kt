package com.toybox.app

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.gamDataStore: DataStore<Preferences> by preferencesDataStore(name = "gamification_prefs")

enum class Badge(val title: String, val emoji: String, val description: String) {
    FIRST_TOY("First Toy!", "🎉", "You added your very first toy!"),
    FIVE_TOYS("Collector!", "⭐", "You have 5 toys in your box!"),
    TEN_TOYS("Super Collector!", "🏆", "Wow, 10 toys and counting!")
}

enum class SurpriseReward(
    val title: String,
    val message: String,
    val emoji: String,
    val bonusPoints: Int = 0
) {
    BONUS_POINTS("Bonus Points! ⭐", "You just earned 50 bonus points!", "⭐", 50),
    GOLDEN_TOY("Golden Toy! 🌟", "Wow! This toy is SUPER special!", "🌟", 0),
    LUCKY_COLLECTOR("Lucky! 🍀", "You're on fire! Keep adding toys!", "🍀", 0),
    MYSTERY_GIFT("Mystery Gift! 🎁", "Something magical happened!", "🎁", 25)
}

@Stable
data class GamificationState(
    val points: Int = 0,
    val earnedBadges: Set<Badge> = emptySet()
)

class GamificationPreferences(private val context: Context) {

    companion object {
        private val POINTS_KEY = intPreferencesKey("points")
        private val BADGES_KEY = stringSetPreferencesKey("earned_badges")
    }

    val state: Flow<GamificationState> = context.gamDataStore.data.map { prefs ->
        val points = prefs[POINTS_KEY] ?: 0
        val badgeNames = prefs[BADGES_KEY] ?: emptySet()
        val badges = badgeNames.mapNotNull { name ->
            Badge.values().find { it.name == name }
        }.toSet()
        GamificationState(points, badges)
    }

    suspend fun addPoints(delta: Int) {
        context.gamDataStore.edit { prefs ->
            prefs[POINTS_KEY] = (prefs[POINTS_KEY] ?: 0) + delta
        }
    }

    suspend fun addBadge(badge: Badge) {
        context.gamDataStore.edit { prefs ->
            prefs[BADGES_KEY] = (prefs[BADGES_KEY] ?: emptySet()) + badge.name
        }
    }

    /** GDPR Art. 17 — wipes all gamification data. */
    suspend fun clearAll() {
        context.gamDataStore.edit { it.clear() }
    }
}
