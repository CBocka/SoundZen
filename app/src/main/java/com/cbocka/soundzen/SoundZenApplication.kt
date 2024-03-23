package com.cbocka.soundzen

import android.app.Application
import com.cbocka.soundzen.utils.Locator

class SoundZenApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Locator.initWith(this)
    }
}