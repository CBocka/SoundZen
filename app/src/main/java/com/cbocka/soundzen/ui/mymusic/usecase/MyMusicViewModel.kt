package com.cbocka.soundzen.ui.mymusic.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.SongRepository
import kotlinx.coroutines.launch
import java.io.File

class MyMusicViewModel : ViewModel() {

    private val state = MutableLiveData<MyMusicListState>()

    fun getState() : MutableLiveData<MyMusicListState> {
        return state
    }

    var allSongs = arrayListOf<Song>()

    fun getSongList() {
        allSongs = SongRepository.instance.getAllSongs(File("/storage/emulated/0/SoundZen"))

        viewModelScope.launch {
            when {
                allSongs.isEmpty() -> state.value = MyMusicListState.NoData
                else -> state.value = MyMusicListState.Success
            }
        }
    }
}