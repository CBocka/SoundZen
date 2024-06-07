package com.cbocka.soundzen.ui.mymusic.favourite_song.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.SongRepository
import com.cbocka.soundzen.utils.FavoritesManager
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteMusicViewModel: ViewModel() {
    private val state = MutableLiveData<FavouriteMusicState>()

    fun getState(): MutableLiveData<FavouriteMusicState> {
        return state
    }

    var favoriteSongs = mutableListOf<Song>()

    fun getSongList() {
        viewModelScope.launch(Dispatchers.IO) {
            val songOrder = Locator.settingsPreferencesRepository.getString(
                Locator.requireApplication.getString(R.string.preference_order_list_key), "SONG"
            )

            favoriteSongs = SongRepository.instance.getFavouriteSongs()

            if (SongRepository.instance.favouritesSongs.isNotEmpty()) {
                favoriteSongs = when (songOrder) {
                    "SONG" -> favoriteSongs.sortedBy { it.songName.lowercase() }.toMutableList()
                    "ARTIST" -> favoriteSongs.sortedBy {
                        if (it.artist == Song.DEFAULT_ARTIST) "zzz" else it.artist.lowercase()
                    }.toMutableList()
                    else -> favoriteSongs
                }
            }

            when {
                favoriteSongs.isEmpty() -> state.postValue(FavouriteMusicState.NoData)
                else -> state.postValue(FavouriteMusicState.Success)
            }
        }
    }

    fun resetState() {
        state.value = FavouriteMusicState.Completed
    }

    fun deleteSong(song: Song) : Boolean {
        FavoritesManager.removeFavorite(Locator.requireApplication, song)
        favoriteSongs.remove(song)
        state.value = if (favoriteSongs.isEmpty()) FavouriteMusicState.NoData else FavouriteMusicState.Success
        return true
    }
}