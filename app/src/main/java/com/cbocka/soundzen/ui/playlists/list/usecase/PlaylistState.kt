package com.cbocka.soundzen.ui.playlist.usecase

sealed class PlaylistState {
    data object NoData : PlaylistState()
    data object Success : PlaylistState()
    data object Completed : PlaylistState()
}
