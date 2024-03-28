package com.cbocka.soundzen.ui.downloadmusic.usecase

sealed class DownloadMusicState {
    data class Loading(val showLoading : Boolean) : DownloadMusicState()
    data object UrlIsMandatory : DownloadMusicState()
    data object SongNameIsMandatory : DownloadMusicState()
    data object ArtistIsMandatory : DownloadMusicState()
    data object UrlNotValid : DownloadMusicState()
    data object SongOK : DownloadMusicState()
    data object Success : DownloadMusicState()
}
