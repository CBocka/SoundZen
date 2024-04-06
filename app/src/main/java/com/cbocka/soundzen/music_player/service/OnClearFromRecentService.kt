package com.cbocka.soundzen.music_player.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.cbocka.soundzen.SoundZenApplication
import com.cbocka.soundzen.music_player.notification.SongNotification
import com.cbocka.soundzen.utils.Locator

class OnClearFromRecentService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }
}