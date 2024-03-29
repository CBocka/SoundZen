package com.cbocka.soundzen.ui.mymusic.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.SongRepository
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MyMusicViewModel : ViewModel() {

    private val state = MutableLiveData<MyMusicListState>()

    fun getState(): MutableLiveData<MyMusicListState> {
        return state
    }

    var allSongs = arrayListOf<Song>()

    fun getSongList() {
        viewModelScope.launch(Dispatchers.IO) {

            if (Locator.loadSongs) {
                state.postValue(MyMusicListState.Loading(true))
                allSongs = SongRepository.instance.getAllSongs(File("/storage/emulated/0/Music"))
                delay(800)
                state.postValue(MyMusicListState.Loading(false))
                delay(100)
            }
            else
                allSongs = SongRepository.instance.allSongs

            when {
                allSongs.isEmpty() -> state.postValue(MyMusicListState.NoData)
                else -> state.postValue(MyMusicListState.Success)
            }
        }
    }

    fun resetState() {
        state.value = MyMusicListState.Completed
    }
}