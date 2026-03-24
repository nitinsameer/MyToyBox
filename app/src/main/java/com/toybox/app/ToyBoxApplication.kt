package com.toybox.app

import android.app.Application

class ToyBoxApplication : Application() {

    lateinit var soundManager: SoundManager

    override fun onCreate() {
        super.onCreate()
        CrashReporter.init(this)
        soundManager = SoundManager(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        soundManager.release()
    }
}
