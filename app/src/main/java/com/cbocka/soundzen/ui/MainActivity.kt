package com.cbocka.soundzen.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.cbocka.soundzen.R
import com.cbocka.soundzen.SoundZenApplication
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.databinding.ActivityMainBinding
import com.cbocka.soundzen.music_player.notification.SongNotification
import com.cbocka.soundzen.music_player.service.MusicService
import com.cbocka.soundzen.music_player.service.MusicService.MusicBinder
import com.cbocka.soundzen.music_player.service.OnClearFromRecentService
import com.cbocka.soundzen.utils.Locator
import java.io.File
import java.io.Serializable
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val PERMISSION_REQUEST_READ_MEDIA_AUDIO = 1
    private lateinit var requestLauncher: String

    lateinit var downloadPath: String

    private var folderPickerContinuation: Continuation<Boolean>? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        val darkTheme: Boolean = Locator.settingsPreferencesRepository.getBoolean(
            getString(R.string.preference_theme_key),
            false
        )
        setTheme(darkTheme)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.contentMain.navBottom, navController)

        checkPermission()

        setAppBarGone()
        setBottomNavVisible()

        registerChannel()
        registerService()
        registerBroadcast()
        initMusicService()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun setAppBarGone() {
        supportActionBar!!.hide()
    }

    fun setAppBarVisible() {
        supportActionBar!!.show()
        binding.appBarLayout.visibility = View.VISIBLE
    }

    fun setBottomNavGone() {
        binding.contentMain.navBottom.visibility = View.GONE
    }

    fun setBottomNavVisible() {
        binding.contentMain.navBottom.visibility = View.VISIBLE
    }

    fun setTheme(darkTheme: Boolean) {
        when (darkTheme) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    suspend fun openFolderPicker(): Boolean = suspendCoroutine { continuation ->
        folderPickerContinuation = continuation
        requestLauncher = "openFolderPicker"
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        openFolderLauncher.launch(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestLauncher == "openFolderPicker") {
            val result = resultCode == Activity.RESULT_OK

            folderPickerContinuation?.resume(result)
            folderPickerContinuation = null
        }
    }

    private val openFolderLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: Intent? = result.data

                intentData?.data?.let { uri ->
                    downloadPath =
                        "/storage/emulated/0/" + uri.path!!.substring(uri.path!!.indexOf(":") + 1)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_READ_MEDIA_AUDIO
            )
        }
    }

    override fun onDestroy() {
        SongNotification.cancelNotification(this)
        super.onDestroy()
    }

    //region MUSIC PLAYER
    private var musicService: MusicService? = null
    private var isServiceBound = false

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicBinder
            musicService = binder.service
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceBound = false
        }
    }

    private val newSongReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == MusicService.ACTION_NEW_SONG) {
                updateSongPlaying()
            }
        }
    }

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (val action = intent.extras!!.getString("action_name")) {
                SongNotification.ACTION_PREVIOUS, SongNotification.ACTION_NEXT -> onSkip(action)

                SongNotification.ACTION_SEEK -> onSeek(intent.extras!!.getLong("new_position"))

                SongNotification.ACTION_PLAY -> if (MusicService.isPlaying) {
                    onSongPause()
                } else {
                    onSongPlay()
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun registerChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SongNotification.CHANNEL_ID, "Song Control",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.setShowBadge(false)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun registerService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        startService(Intent(baseContext, OnClearFromRecentService::class.java))
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerBroadcast() {
        registerReceiver(newSongReceiver, IntentFilter(MusicService.ACTION_NEW_SONG))

        registerReceiver(notificationReceiver, IntentFilter("TRACKS_TRACKS"))
    }

    private fun initMusicService() {
        binding.contentMain.imgPauseContinue.setOnClickListener { v ->
            if (MusicService.exoPlayer!!.isPlaying) {
                onSongPause()
            } else {
                onSongPlay()
            }
        }

        binding.contentMain.imgNext.setOnClickListener { v -> onSkip(SongNotification.ACTION_NEXT) }

        binding.contentMain.imgPrevious.setOnClickListener { v -> onSkip(SongNotification.ACTION_PREVIOUS) }
    }

    fun startPlayer(files: List<Song>) {
        binding.contentMain.cvSongPlaying.visibility = View.VISIBLE

        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("music_files", files as Serializable?)
        startService(intent)
    }

    fun updateSongPlaying() {
        onSongPlay()
    }

    private fun onSkip(action: String) {
        when (action) {
            SongNotification.ACTION_PREVIOUS -> musicService!!.playPrevious()
            SongNotification.ACTION_NEXT -> musicService!!.playNext()
        }
    }

    private fun onSeek(newPos: Long) {
        musicService!!.seekTo(newPos)

        SongNotification.updateNotification(Locator.requireApplication, this)
    }

    private fun onSongPause() {
        musicService!!.pause()

        binding.contentMain.imgPauseContinue.setImageResource(R.drawable.ic_play)
        SongNotification.updateNotification(Locator.requireApplication, this)
    }

    private fun onSongPlay() {
        musicService!!.resume()

        binding.contentMain.tvSongName.text = MusicService.musicFiles[MusicService.currentSongIndex].songName
        binding.contentMain.tvSongArtist.text = MusicService.musicFiles[MusicService.currentSongIndex].artist

        binding.contentMain.imgPauseContinue.setImageResource(R.drawable.ic_pause)

        if (SongNotification.getInstance().notification == null)
            SongNotification.createNotification(Locator.requireApplication, this)

        else
            SongNotification.updateNotification(Locator.requireApplication, this)
    }
    //endregion
}