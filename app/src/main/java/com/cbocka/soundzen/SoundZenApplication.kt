package com.cbocka.soundzen

import android.app.Application
import androidx.core.content.ContextCompat
import com.cbocka.soundzen.utils.Locator
import java.io.File
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.cbocka.soundzen.music_player.notification.SongNotification
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.utils.FavoritesManager

class SoundZenApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Locator.initWith(this)
        createDirectory()

        FavoritesManager.loadFavorites(this)
    }

    private fun createDirectory() {
        val path = "/storage/emulated/0/Music/"

        val directory = File(path)

        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    override fun onTerminate() {
        SongNotification.cancelNotification(this)
        super.onTerminate()
    }
}