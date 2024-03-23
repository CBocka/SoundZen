package com.cbocka.soundzen.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataStorePreferencesRepository(private val dataStore: DataStore<Preferences>) : PreferenceDataStore() {

    override fun putString(key: String, value: String?) {
        putSettingValue(key, value)
    }

    override fun getString(key: String, defValue: String?): String? {
        return getSettingValue(key, defValue.orEmpty())
    }

    override fun putStringSet(key: String, values: MutableSet<String>?) {
        putSettingValue(key, values.multiSelectToString())
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String> {
        val value = getSettingValue(key, defValues.multiSelectToString())
        return value.stringToMultiSelect()
    }

    override fun putInt(key: String, value: Int) {
        putSettingValue(key, value.toString())
    }

    override fun getInt(key: String, defValue: Int): Int {
        return getSettingValue(key, defValue.toString())?.toInt() ?: 0
    }

    override fun putBoolean(key: String, value: Boolean) {
        putSettingValue(key, value.toString())
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return getSettingValue(key, defValue.toString()).toBoolean()
    }

    private fun putSettingValue(key: String, value: String?) {
        val keyPref = stringPreferencesKey(key)
        CoroutineScope(Dispatchers.IO).launch {
            // Save the value somewhere
            dataStore.edit { preferences ->
                preferences[keyPref] = value ?: "none"
            }
        }
    }

    private fun getSettingValue (key: String, defValue: String?): String? =
        runBlocking {
            val keyPref = stringPreferencesKey(key)

            dataStore.data.map { preferences ->
                preferences[keyPref] ?: "none"
            }.first()
        }

    private fun Set<String>?.multiSelectToString(): String {
        return this?.joinToString(",").orEmpty()
    }

    private fun String?.stringToMultiSelect(): Set<String> {
        return this?.split(",")?.toSet() ?: setOf()
    }
}