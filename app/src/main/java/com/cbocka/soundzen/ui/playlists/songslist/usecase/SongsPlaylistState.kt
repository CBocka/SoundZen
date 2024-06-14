package com.cbocka.soundzen.ui.playlists.songslist.usecase

sealed class SongsPlaylistState {
    data object NoData : SongsPlaylistState()
    data object Success : SongsPlaylistState()
    data object Completed : SongsPlaylistState()
}
