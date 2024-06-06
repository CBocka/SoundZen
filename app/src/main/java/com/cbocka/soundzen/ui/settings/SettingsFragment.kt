package com.cbocka.soundzen.ui.settings

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.cbocka.soundzen.R
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        initPreferenceTheme()
        initOrderPreference()
        initPlayerOrderPreference()
        initLocationPathPreference()
    }

    private fun initPreferenceTheme() {
        val theme = preferenceManager.findPreference<SwitchPreferenceCompat>(getString(R.string.preference_theme_key))
        theme?.isChecked = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key),true)

        theme?.setOnPreferenceChangeListener { _, newBoolean ->
            Locator.settingsPreferencesRepository.putBoolean(getString(R.string.preference_theme_key),newBoolean as Boolean)
            (activity as MainActivity).setTheme(newBoolean)
            true
        }
    }

    private fun initOrderPreference() {
        val orderList = preferenceManager.findPreference<ListPreference>(getString(R.string.preference_order_list_key))
        orderList?.value = Locator.settingsPreferencesRepository.getString(getString(R.string.preference_order_list_key),"SONG")

        orderList?.setOnPreferenceChangeListener { _, newValue ->
            Locator.settingsPreferencesRepository.putString(getString(R.string.preference_order_list_key), newValue as String)
            true
        }
    }

    private fun initPlayerOrderPreference() {
        val playerOrder = preferenceManager.findPreference<ListPreference>(getString(R.string.preference_player_order_key))
        playerOrder?.value = Locator.settingsPreferencesRepository.getString(getString(R.string.preference_player_order_key),"SEC")

        playerOrder?.setOnPreferenceChangeListener { _, newValue ->
            Locator.settingsPreferencesRepository.putString(getString(R.string.preference_player_order_key), newValue as String)
            true
        }
    }

    private fun initLocationPathPreference() {
        val location = preferenceManager.findPreference<Preference>(getString(R.string.preference_location_path_key))
        location?.summary = Locator.settingsPreferencesRepository.getString(getString(R.string.preference_location_path_key),"/storage/emulated/0/Music/")

        location!!.setOnPreferenceClickListener {
            lifecycleScope.launch {
                (activity as MainActivity).openFolderPicker()

                Locator.settingsPreferencesRepository.putString(getString(R.string.preference_location_path_key), (activity as MainActivity).downloadPath)
                location.summary = (activity as MainActivity).downloadPath

                Locator.loadSongs = true
                Locator.loadDirectories = true
                Locator.loadSongsFromDirectory = true
            }
            true
        }
    }
}

