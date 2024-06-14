package com.cbocka.soundzen.data.model

data class Playlist(val name: String, var songsIncluded: List<Song>) {
    companion object {
        const val PLAYLIST_KEY = "PLAYLIST_KEY"
    }
}
