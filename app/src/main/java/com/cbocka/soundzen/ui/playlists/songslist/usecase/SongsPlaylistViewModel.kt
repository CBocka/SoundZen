package com.cbocka.soundzen.ui.playlists.songslist.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.PlaylistRepository
import com.cbocka.soundzen.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongsPlaylistViewModel: ViewModel() {
    private val state = MutableLiveData<SongsPlaylistState>()

    fun getState(): MutableLiveData<SongsPlaylistState> {
        return state
    }

    var songsOnPlaylist = mutableListOf<Song>()
    lateinit var playlist: String

    fun getSongList(playlistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val songOrder = Locator.settingsPreferencesRepository.getString(
                Locator.requireApplication.getString(R.string.preference_order_list_key), "SONG"
            )

            songsOnPlaylist = PlaylistRepository.instance.getSongsFromPlaylist(playlistName)!!.toMutableList()

            if (songsOnPlaylist.isNotEmpty())
                when (songOrder) {
                    "SONG" ->
                        songsOnPlaylist =
                            songsOnPlaylist.sortedBy { it.songName.lowercase() } as MutableList<Song>

                    "ARTIST" ->
                        songsOnPlaylist = songsOnPlaylist.sortedBy {
                            if (it.artist == Song.DEFAULT_ARTIST)
                                "zzz"
                            else
                                it.artist.lowercase()
                        } as MutableList<Song>
                }

            when {
                songsOnPlaylist.isEmpty() -> state.postValue(SongsPlaylistState.NoData)
                else -> state.postValue(SongsPlaylistState.Success)
            }
        }
    }

    fun resetState() {
        state.value = SongsPlaylistState.Completed
    }

    fun deleteSong(song: Song) : Boolean {
        PlaylistRepository.instance.removeSongFromPlaylist(playlist, song)

        getSongList(playlist)

        if (songsOnPlaylist.isEmpty()) state.value = SongsPlaylistState.NoData
        return true
    }
}