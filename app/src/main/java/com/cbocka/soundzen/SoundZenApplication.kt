package com.cbocka.soundzen

import android.app.Application
import androidx.core.content.ContextCompat
import com.cbocka.soundzen.utils.Locator
import java.io.File
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class SoundZenApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Locator.initWith(this)
        createDirectory()
    }

    fun createDirectory() {
        val path = "/storage/emulated/0/Music/"

        val directory = File(path)

        if (!directory.exists()) {
            directory.mkdirs()
        }
    }
}