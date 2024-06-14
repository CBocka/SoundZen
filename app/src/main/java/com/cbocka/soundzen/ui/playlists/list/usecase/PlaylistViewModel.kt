package com.cbocka.soundzen.ui.playlist.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.data.model.Playlist
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.PlaylistRepository
import com.cbocka.soundzen.ui.mymusic.favourite_song.usecase.FavouriteMusicState
import com.cbocka.soundzen.utils.FavoritesManager
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel: ViewModel() {

    private val state = MutableLiveData<PlaylistState>()

    fun getState(): MutableLiveData<PlaylistState> {
        return state
    }

    var allPlaylist = mutableMapOf<String, List<Song>>()

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {

            allPlaylist = PlaylistRepository.instance.getPlaylists()

            when {
                allPlaylist.isEmpty() -> state.postValue(PlaylistState.NoData)
                else -> state.postValue(PlaylistState.Success)
            }
        }
    }

    fun resetState() {
        state.value = PlaylistState.Completed
    }

    fun deletePlaylist(playlist: Playlist) : Boolean {
        PlaylistRepository.instance.deletePlaylist(playlist.name)
        allPlaylist.remove(playlist.name)
        state.value = if (allPlaylist.isEmpty()) PlaylistState.NoData else PlaylistState.Success
        return true
    }
}