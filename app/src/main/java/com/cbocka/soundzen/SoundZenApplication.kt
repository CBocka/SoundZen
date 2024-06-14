package com.cbocka.soundzen

import android.app.Application
import com.cbocka.soundzen.utils.Locator
import java.io.File
import com.cbocka.soundzen.music_player.notification.SongNotification
import com.cbocka.soundzen.utils.FavoritesManager
import com.cbocka.soundzen.utils.PlaylistsManager

class SoundZenApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Locator.initWith(this)
        createDirectory()

        FavoritesManager.loadFavorites(this)
        PlaylistsManager.loadPlaylists(this)
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