package com.cbocka.soundzen.music_player.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat
import com.cbocka.soundzen.R
import com.cbocka.soundzen.music_player.service.MusicService
import com.cbocka.soundzen.ui.MainActivity

class SongNotification private constructor() {
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var mediaSession: MediaSessionCompat
    var notification: androidx.core.app.NotificationCompat.Builder? = null

    companion object {
        private val instance = SongNotification()

        fun getInstance() : SongNotification {
            return instance
        }

        const val CHANNEL_ID = "song_channel"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_PLAY = "action_play"
        const val ACTION_NEXT = "action_next"
        const val ACTION_SEEK = "action_seek"


        @SuppressLint("MissingPermission")
        fun createNotification(context: Context, activity: MainActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                instance.notificationManagerCompat = NotificationManagerCompat.from(context)
                val mediaSessionCompat: MediaSessionCompat = createMediaSession(context, activity)
                val icon = BitmapFactory.decodeResource(context.resources, R.drawable.soundzen)

                // Crear notificacion
                instance.notification = androidx.core.app.NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.soundzen)
                    .setContentTitle(MusicService.musicFiles[MusicService.currentSongIndex].songName)
                    .setContentText(MusicService.musicFiles[MusicService.currentSongIndex].artist)
                    .setLargeIcon(icon)
                    .setStyle(
                        NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.sessionToken)
                    )
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setPriority(PRIORITY_MAX)
                    .setSilent(true)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setOngoing(true)

                instance.notificationManagerCompat.notify(1, instance.notification!!.build())
            }
        }

        @SuppressLint("MissingPermission")
        fun updateNotification(context: Context, activity: MainActivity) {
            val mediaSessionCompat: MediaSessionCompat = createMediaSession(context, activity)

            instance.notification!!
                .setContentTitle(MusicService.musicFiles[MusicService.currentSongIndex].songName)
                .setContentText(MusicService.musicFiles[MusicService.currentSongIndex].artist)
                .setStyle(
                    NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.sessionToken)
                )

            instance.notificationManagerCompat.notify(1, instance.notification!!.build())
        }

        private fun createMediaSession(
            context: Context,
            activity: MainActivity
        ): MediaSessionCompat {
            instance.mediaSession = MediaSessionCompat(context, "tag")
            instance.mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, MusicService.exoPlayer!!.duration)
                    .build()
            )
            instance.mediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
            instance.mediaSession.setFlags(0)

            val playbackState: PlaybackStateCompat = if (MusicService.isPlaying) {
                PlaybackStateCompat.Builder()
                    .setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        MusicService.exoPlayer!!.currentPosition,
                        1.0f
                    )
                    .setActions(
                        PlaybackStateCompat.ACTION_SEEK_TO or
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                                PlaybackStateCompat.ACTION_PAUSE or
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                                PlaybackStateCompat.ACTION_STOP
                    )
                    .build()
            } else {
                PlaybackStateCompat.Builder()
                    .setState(
                        PlaybackStateCompat.STATE_PAUSED,
                        MusicService.exoPlayer!!.currentPosition,
                        1.0f
                    )
                    .setActions(
                        PlaybackStateCompat.ACTION_SEEK_TO or
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                                PlaybackStateCompat.ACTION_PLAY or
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                    .build()
            }

            instance.mediaSession.setPlaybackState(playbackState)

            instance.mediaSession.setCallback(object : MediaSessionCompat.Callback() {
                override fun onSeekTo(pos: Long) {
                    //instance.mediaSession.release()

                    val intent = Intent("TRACKS_TRACKS")
                        .putExtra("action_name", ACTION_SEEK)
                        .putExtra("new_position", pos)

                    activity.sendBroadcast(intent)
                }

                override fun onSkipToPrevious() {
                    //instance.mediaSession.release()

                    val intent = Intent("TRACKS_TRACKS").putExtra("action_name", ACTION_PREVIOUS)
                    activity.sendBroadcast(intent)
                }

                override fun onSkipToNext() {
                    //instance.mediaSession.release()

                    val intent = Intent("TRACKS_TRACKS").putExtra("action_name", ACTION_NEXT)
                    activity.sendBroadcast(intent)
                }

                override fun onPlay() {
                    //instance.mediaSession.release()

                    val intent = Intent("TRACKS_TRACKS").putExtra("action_name", ACTION_PLAY)
                    activity.sendBroadcast(intent)
                }

                override fun onPause() {
                    //instance.mediaSession.release()

                    val intent = Intent("TRACKS_TRACKS").putExtra("action_name", ACTION_PLAY)
                    activity.sendBroadcast(intent)
                }
            })

            instance.mediaSession.isActive = true

            return instance.mediaSession
        }

        fun cancelNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.cancel(1)
        }
    }
}