package com.cbocka.soundzen.music_player.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.music_player.notification.SongNotification
import com.cbocka.soundzen.utils.Locator
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import java.io.File

class MusicService : Service() {
    companion object {
        const val ACTION_NEW_SONG = "com.soundzen.NEW_SONG"
        lateinit var musicFiles: List<Song>
        var currentSongIndex = 0
        var exoPlayer: ExoPlayer? = null
        var firstTimeSongReady: Boolean = true
        var isPlaying: Boolean = true
    }

    private val binder: IBinder = MusicBinder()

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentSongIndex = 0
        firstTimeSongReady = true

        if (intent != null && intent.hasExtra("music_files")) {
            musicFiles = intent.getSerializableExtra("music_files") as List<Song>
            playMusic()
        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        exoPlayer = SimpleExoPlayer.Builder(Locator.requireApplication).build()
        exoPlayer!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    playNext()
                } else if (playbackState == ExoPlayer.STATE_READY && firstTimeSongReady) { //exoPlayer.currentPosition == 0L
                    firstTimeSongReady = false

                    exoPlayer!!.play()
                    val i = Intent(ACTION_NEW_SONG)
                    sendBroadcast(i)
                }
            }
        })
    }

    private fun playMusic() {
        isPlaying = true

        exoPlayer!!.stop()
        exoPlayer!!.clearMediaItems()

        val musicFilePath = musicFiles[currentSongIndex].file.path
        val mediaItem: MediaItem = MediaItem.fromUri(musicFilePath)
        exoPlayer!!.setMediaItem(mediaItem)
        exoPlayer!!.prepare()
    }

    fun pause() {
        isPlaying = false
        exoPlayer!!.pause()
    }

    fun resume() {
        isPlaying = true
        exoPlayer!!.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer!!.seekTo(positionMs)
    }

    fun playNext() {
        currentSongIndex = (currentSongIndex + 1) % musicFiles.size
        firstTimeSongReady = true

        playMusic()
    }

    fun playPrevious() {
        currentSongIndex =
            if (currentSongIndex - 1 == -1) musicFiles.size - 1 else currentSongIndex - 1
        firstTimeSongReady = true

        playMusic()
    }
}