package com.cbocka.soundzen.music_player.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.utils.Locator
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class MusicService : Service() {

    companion object {
        const val ACTION_NEW_SONG = "com.soundzen.NEW_SONG"
        lateinit var musicFiles: List<Song>
        var currentSongIndex = 0
        var exoPlayer: ExoPlayer? = null
        var firstTimeSongReady: Boolean = true
        var isPlaying: Boolean = true
        var isLooping = false
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                musicFiles = intent.getParcelableArrayListExtra("music_files", Song::class.java) as List<Song>

            } else {
                val serializableExtra = intent.getSerializableExtra("music_files")

                if (serializableExtra is List<*>) {
                    musicFiles = serializableExtra as List<Song>
                }
            }

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
                    playNextOrLoop()
                } else if (playbackState == ExoPlayer.STATE_READY && firstTimeSongReady) {
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

        val musicFilePath = musicFiles[currentSongIndex].filePath
        val mediaItem: MediaItem = MediaItem.fromUri(musicFilePath)
        exoPlayer!!.setMediaItem(mediaItem)
        exoPlayer!!.prepare()
        exoPlayer!!.play()
    }

    fun updateMusicFiles(newMusicFiles: List<Song>) {
        musicFiles = newMusicFiles
        currentSongIndex = 0

        if (exoPlayer!!.isPlaying) {
            playMusic()
        }
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

    fun setLooping(looping: Boolean) {
        isLooping = looping
        exoPlayer!!.repeatMode = if (looping) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
    }

    fun playNextOrLoop() {
        if (isLooping) {
            exoPlayer!!.seekTo(0)
            exoPlayer!!.play()
        } else {
            playNext()
        }
    }
}



