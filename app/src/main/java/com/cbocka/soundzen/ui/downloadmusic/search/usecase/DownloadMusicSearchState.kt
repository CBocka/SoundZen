package com.cbocka.soundzen.ui.downloadmusic.search.usecase

sealed class DownloadMusicSearchState {
    data class Loading(val showLoading : Boolean, val title: String) : DownloadMusicSearchState()
    data object SongNameIsMandatory : DownloadMusicSearchState()
    data object ArtistIsMandatory : DownloadMusicSearchState()
    data object SongNotOK : DownloadMusicSearchState()
    data object SongOK : DownloadMusicSearchState()
    data object Success : DownloadMusicSearchState()
    data object Completed : DownloadMusicSearchState()
}
