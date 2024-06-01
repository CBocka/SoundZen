package com.cbocka.soundzen.ui.mymusic.song_in_directory.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.SongRepository
import com.cbocka.soundzen.ui.mymusic.all_music.usecase.MyMusicListState
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class SongsInDirectoryViewModel : ViewModel() {
    private val state = MutableLiveData<SongsInDirectoryState>()

    fun getState(): MutableLiveData<SongsInDirectoryState> {
        return state
    }

    var allSongs = mutableListOf<Song>()
    var filteredSongs: List<Song> = allSongs

    var directoryPath : String = ""

    fun getSongList() {
        viewModelScope.launch(Dispatchers.IO) {
            val songOrder = Locator.settingsPreferencesRepository.getString(
                Locator.requireApplication.getString(R.string.preference_order_list_key), "SONG"
            )

            state.postValue(SongsInDirectoryState.Loading(true))

            allSongs = SongRepository.instance.getSongsFromDirectory(File(directoryPath))

            delay(800)
            state.postValue(SongsInDirectoryState.Loading(false))
            delay(100)

            if (allSongs.isNotEmpty()) {
                allSongs = when (songOrder) {
                    "SONG" -> allSongs.sortedBy { it.songName.lowercase() }.toMutableList()
                    "ARTIST" -> allSongs.sortedBy {
                        if (it.artist == Song.DEFAULT_ARTIST)
                            "zzz"
                        else
                            it.artist.lowercase()
                    }.toMutableList()
                    else -> allSongs
                }
            }

            when {
                allSongs.isEmpty() -> state.postValue(SongsInDirectoryState.NoData)
                else -> state.postValue(SongsInDirectoryState.Success)
            }
        }
    }

    fun resetState() {
        state.value = SongsInDirectoryState.Completed
    }

    fun deleteSong(song: Song) : Boolean {
        Locator.loadSongs = true
        return SongRepository.instance.deleteSong(song)
    }
}