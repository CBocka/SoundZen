package com.cbocka.soundzen.ui.downloadmusic.yt.usecase

sealed class DownloadMusicYTState {
    data class Loading(val showLoading : Boolean) : DownloadMusicYTState()
    data object UrlIsMandatory : DownloadMusicYTState()
    data object SongNameIsMandatory : DownloadMusicYTState()
    data object ArtistIsMandatory : DownloadMusicYTState()
    data object UrlNotValid : DownloadMusicYTState()
    data object SongOK : DownloadMusicYTState()
    data object Success : DownloadMusicYTState()
    data object Completed : DownloadMusicYTState()
}
