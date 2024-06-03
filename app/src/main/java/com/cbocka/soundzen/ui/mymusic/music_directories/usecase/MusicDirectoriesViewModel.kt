package com.cbocka.soundzen.ui.mymusic.music_directories.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.MusicDirectory
import com.cbocka.soundzen.data.repository.SongRepository
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MusicDirectoriesViewModel : ViewModel() {

    private val state = MutableLiveData<MusicDirectoriesState>()

    fun getState(): MutableLiveData<MusicDirectoriesState> {
        return state
    }

    var allDirectories = mutableListOf<MusicDirectory>()

    fun getDirectoriesList() {
        viewModelScope.launch(Dispatchers.IO) {

            if (Locator.loadDirectories) {
                state.postValue(MusicDirectoriesState.Loading(true))

                allDirectories = SongRepository.instance.getAllDirectories(
                    File(
                        Locator.settingsPreferencesRepository.getString(
                            Locator.requireApplication.getString(R.string.preference_location_path_key),
                            "/storage/emulated/0/Music/"
                        )!!
                    )
                )

                delay(800)
                state.postValue(MusicDirectoriesState.Loading(false))
                delay(100)
            }

            when {
                allDirectories.isEmpty() -> state.postValue(MusicDirectoriesState.NoData)
                else -> state.postValue(MusicDirectoriesState.Success)
            }
        }
    }

    fun resetState() {
        state.value = MusicDirectoriesState.Completed
    }
}