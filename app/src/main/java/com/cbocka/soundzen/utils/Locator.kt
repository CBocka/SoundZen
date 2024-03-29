package com.cbocka.soundzen.utils

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.cbocka.soundzen.preferences.DataStorePreferencesRepository

object Locator {

    private var application : Application? = null

    private val requireApplication get() = application ?: error("Missing call: initWith(application)")

    fun initWith(application: Application) {
        this.application = application
    }

    private val Context.settingsSore by preferencesDataStore("settings")

    val settingsPreferencesRepository by lazy {
        DataStorePreferencesRepository(requireApplication.settingsSore)
    }

    var loadSongs : Boolean = true
}