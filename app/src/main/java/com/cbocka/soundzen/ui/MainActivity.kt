package com.cbocka.soundzen.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
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
import com.cbocka.soundzen.databinding.ActivityMainBinding
import com.cbocka.soundzen.utils.Locator
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
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                PERMISSION_REQUEST_READ_MEDIA_AUDIO
            )
        }
    }
}