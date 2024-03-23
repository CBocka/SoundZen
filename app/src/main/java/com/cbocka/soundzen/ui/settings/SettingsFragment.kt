package com.cbocka.soundzen.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.cbocka.soundzen.R
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.utils.Locator

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        initPreferenceTheme()
    }

    private fun initPreferenceTheme() {
        val theme = preferenceManager.findPreference<SwitchPreferenceCompat>(getString(R.string.preference_theme_key))
        theme?.isChecked = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key),false)

        theme?.setOnPreferenceChangeListener() { _, newBoolean ->
            Locator.settingsPreferencesRepository.putBoolean(getString(R.string.preference_theme_key),newBoolean as Boolean)
            (activity as MainActivity).setTheme(newBoolean)
            true
        }
    }
}