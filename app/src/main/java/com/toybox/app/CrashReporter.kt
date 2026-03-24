package com.toybox.app

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Utility class for centralized crash reporting and logging
 */
object CrashReporter {
    private var initialized = false

    /**
     * Called at app startup — collection is OFF until the parent explicitly grants consent
     * on the SetupScreen. This satisfies COPPA §312.5 and GDPR Art. 8.
     */
    fun init(context: Context) {
        if (!initialized) {
            try {
                // Disabled by default — enabled only after verifiable parental consent
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)

                // Plant Timber tree so local debug logs still work; Crashlytics is gated
                Timber.plant(CrashlyticsTree())

                initialized = true
            } catch (e: Exception) {
                android.util.Log.e("CrashReporter", "Failed to initialize: ${e.message}", e)
            }
        }
    }

    /**
     * Called once after the parent ticks the consent checkbox on SetupScreen.
     * Safe to call multiple times (idempotent).
     */
    fun enableCollection() {
        try {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        } catch (e: Exception) {
            android.util.Log.e("CrashReporter", "enableCollection failed: ${e.message}", e)
        }
    }

    /**
     * Called when the parent deletes all data — immediately halts further collection.
     */
    fun disableCollection() {
        try {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        } catch (e: Exception) {
            android.util.Log.e("CrashReporter", "disableCollection failed: ${e.message}", e)
        }
    }

    fun logError(tag: String, message: String, exception: Throwable? = null) {
        Timber.e(exception, "$tag: $message")
        exception?.let {
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }

    fun logWarning(tag: String, message: String) {
        Timber.w("$tag: $message")
    }

    fun logInfo(tag: String, message: String) {
        Timber.i("$tag: $message")
    }

    /**
     * Custom Timber tree for Firebase Crashlytics integration
     */
    private class CrashlyticsTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            try {
                FirebaseCrashlytics.getInstance().apply {
                    setCustomKey("priority", priority)
                    setCustomKey("tag", tag ?: "")
                    log(message)
                    if (t != null) {
                        recordException(t)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CrashlyticsTree", "Error logging to Firebase", e)
            }
        }
    }
}

