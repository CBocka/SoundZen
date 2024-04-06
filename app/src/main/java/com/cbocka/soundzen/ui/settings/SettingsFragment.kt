package com.cbocka.soundzen.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.cbocka.soundzen.R
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.utils.Locator

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        initPreferenceTheme()
        initOrderPreference()
        initPlayerOrderPreference()
    }

    private fun initPreferenceTheme() {
        val theme = preferenceManager.findPreference<SwitchPreferenceCompat>(getString(R.string.preference_theme_key))
        theme?.isChecked = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key),false)

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
}