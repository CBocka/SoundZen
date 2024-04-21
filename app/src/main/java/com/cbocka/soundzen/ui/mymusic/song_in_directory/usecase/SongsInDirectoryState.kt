package com.cbocka.soundzen.ui.mymusic.song_in_directory.usecase

sealed class SongsInDirectoryState {
    data object NoData : SongsInDirectoryState()
    data class Loading(val show : Boolean) : SongsInDirectoryState()
    data object Success : SongsInDirectoryState()
    data object Completed : SongsInDirectoryState()
}
