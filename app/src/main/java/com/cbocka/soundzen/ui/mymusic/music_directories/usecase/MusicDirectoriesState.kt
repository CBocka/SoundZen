package com.cbocka.soundzen.ui.mymusic.music_directories.usecase

sealed class MusicDirectoriesState {
    data object NoData : MusicDirectoriesState()
    data class Loading(val show : Boolean) : MusicDirectoriesState()
    data object Success : MusicDirectoriesState()
    data object Completed : MusicDirectoriesState()
}