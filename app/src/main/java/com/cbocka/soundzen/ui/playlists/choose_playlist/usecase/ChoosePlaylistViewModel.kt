package com.cbocka.soundzen.ui.playlists.choose_playlist.usecase

import androidx.lifecycle.ViewModel
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.PlaylistRepository

class ChoosePlaylistViewModel: ViewModel() {

    fun getPlaylists(): Map<String, List<Song>> {
        return PlaylistRepository.instance.allPlaylist
    }
}