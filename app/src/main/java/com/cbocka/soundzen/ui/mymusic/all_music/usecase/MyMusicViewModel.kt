package com.cbocka.soundzen.ui.mymusic.all_music.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.SongRepository
import com.cbocka.soundzen.utils.FavoritesManager
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MyMusicViewModel() : ViewModel() {

    private val state = MutableLiveData<MyMusicListState>()

    fun getState(): MutableLiveData<MyMusicListState> {
        return state
    }

    var allSongs = mutableListOf<Song>()
    var filteredSongs: List<Song> = allSongs

    fun getSongList() {
        viewModelScope.launch(Dispatchers.IO) {
            val songOrder = Locator.settingsPreferencesRepository.getString(
                Locator.requireApplication.getString(R.string.preference_order_list_key), "SONG"
            )

            if (Locator.loadSongs) {
                state.postValue(MyMusicListState.Loading(true))

                SongRepository.instance.getAllSongs(File(
                    Locator.settingsPreferencesRepository.getString(
                        Locator.requireApplication.getString(R.string.preference_location_path_key),
                        "/storage/emulated/0/Music/")!!))

                delay(800)
                state.postValue(MyMusicListState.Loading(false))
                delay(100)
            }

            if (SongRepository.instance.allSongs.isNotEmpty())
                when (songOrder) {
                    "SONG" ->
                        allSongs =
                            SongRepository.instance.allSongs.sortedBy { it.songName.lowercase() } as MutableList<Song>

                    "ARTIST" ->
                        allSongs = SongRepository.instance.allSongs.sortedBy {
                            if (it.artist == Song.DEFAULT_ARTIST)
                                "zzz"
                            else
                                it.artist.lowercase()
                        } as MutableList<Song>
                }

            when {
                allSongs.isEmpty() -> state.postValue(MyMusicListState.NoData)
                else -> state.postValue(MyMusicListState.Success)
            }
        }
    }

    fun resetState() {
        state.value = MyMusicListState.Completed
    }

    fun deleteSong(song: Song) : Boolean {
        Locator.loadSongs = true
        return SongRepository.instance.deleteSong(song)
    }

    fun addSongToFavourites(song: Song) {
        FavoritesManager.addFavorite(Locator.requireApplication, song)
    }

    fun removeSongFromFavourites(song: Song) {
        FavoritesManager.removeFavorite(Locator.requireApplication, song)
    }

    fun isFavourite(song: Song): Boolean {
        return FavoritesManager.isFavorite(Locator.requireApplication, song)
    }
}
