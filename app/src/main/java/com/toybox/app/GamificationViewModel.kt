package com.toybox.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class GamificationViewModel(app: Application) : AndroidViewModel(app) {

    private val gamRepo       = GamificationRepository(app)
    private val challengeRepo = ChallengeRepository(app)

    // ── Persistent state ──────────────────────────────────────────────────────

    val state: StateFlow<GamificationState> = gamRepo.state
        .stateIn(viewModelScope, SharingStarted.Eagerly, GamificationState())

    val challengeProgresses: StateFlow<List<ChallengeProgress>> = challengeRepo.challenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── One-shot UI events ────────────────────────────────────────────────────

    private val _newBadge = MutableStateFlow<Badge?>(null)
    val newBadge: StateFlow<Badge?> = _newBadge.asStateFlow()

    private val _surprise = MutableStateFlow<SurpriseReward?>(null)
    val surprise: StateFlow<SurpriseReward?> = _surprise.asStateFlow()

    private val _completedChallenge = MutableStateFlow<ChallengeProgress?>(null)
    val completedChallenge: StateFlow<ChallengeProgress?> = _completedChallenge.asStateFlow()

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Call immediately after a toy is added.
     * [newToyCount] = total count AFTER addition.
     * [toy]         = the toy that was just added.
     */
    fun onToyAdded(newToyCount: Int, toy: Toy) {
        viewModelScope.launch {

            // 1. Badge + base +10 pts
            val earned = gamRepo.onToyAdded(newToyCount, state.value)
            if (earned != null) _newBadge.value = earned

            // 2. Challenge evaluation
            val completedList = challengeRepo.onToyAdded(toy, challengeProgresses.value)
            completedList.firstOrNull()?.let { done ->
                gamRepo.addPoints(done.challenge.rewardPoints)
                _completedChallenge.value = done
            }

            // 3. Surprise (20% chance)
            if (Random.nextFloat() < 0.20f) {
                val reward = SurpriseReward.values().random()
                if (reward.bonusPoints > 0) gamRepo.addPoints(reward.bonusPoints)
                _surprise.value = reward
            }
        }
    }

    fun dismissBadge()     { _newBadge.value = null }
    fun dismissSurprise()  { _surprise.value = null }
    fun dismissChallenge() { _completedChallenge.value = null }

    /** GDPR Art. 17 — clears all gamification + challenge data. */
    fun clearAllData() {
        viewModelScope.launch {
            gamRepo.clearAll()
            challengeRepo.clearAll()
        }
    }
}
