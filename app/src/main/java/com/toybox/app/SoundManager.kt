package com.toybox.app

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager

/**
 * Lifecycle-safe sound manager using system notification sounds.
 * No raw assets required — uses device's default notification URI.
 *
 * Create once in [ToyBoxApplication] and reuse across the app.
 */
class SoundManager(private val context: Context) {

    private var player: MediaPlayer? = null

    /** Plays a cheerful notification sound at full volume when a toy is added. */
    fun playAddSound() = play(volume = 0.75f)

    /** Plays the same sound at low volume for a soft delete feedback. */
    fun playDeleteSound() = play(volume = 0.25f)

    private fun play(volume: Float) {
        try {
            player?.release()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            player = MediaPlayer.create(context, uri)?.apply {
                setVolume(volume, volume)
                setOnCompletionListener { it.release() }
                start()
            }
        } catch (_: Exception) {
            // Sound is non-critical — never crash the app over it
        }
    }

    fun release() {
        player?.release()
        player = null
    }
}
