package com.cbocka.soundzen.data.repository

import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.utils.Locator
import com.cbocka.soundzen.utils.PlaylistsManager

class PlaylistRepository private constructor() {
    var allPlaylist = mutableMapOf<String, List<Song>>()

    companion object {
        val instance = PlaylistRepository()
    }

    fun getPlaylists(): MutableMap<String, List<Song>> {
        allPlaylist = PlaylistsManager.loadPlaylists(Locator.requireApplication)
        return allPlaylist
    }

    fun addPlaylist(playlistName: String) {
        val context = Locator.requireApplication
        if (!allPlaylist.containsKey(playlistName)) {
            allPlaylist[playlistName] = mutableListOf<Song>()
            PlaylistsManager.savePlaylists(context, allPlaylist)
        }
    }

    fun deletePlaylist(playlistName: String) {
        val context = Locator.requireApplication
        if (allPlaylist.containsKey(playlistName)) {
            allPlaylist.remove(playlistName)
            PlaylistsManager.savePlaylists(context, allPlaylist)
        }
    }

    fun getSongsFromPlaylist(playlistName: String): List<Song>? {
        val context = Locator.requireApplication
        return PlaylistsManager.getSongsFromPlaylist(context, playlistName)
    }

    fun addSongToPlaylist(playlistName: String, song: Song) {
        val context = Locator.requireApplication
        val currentSongs = allPlaylist[playlistName]?.toMutableList() ?: mutableListOf()
        if (!currentSongs.any { it.filePath == song.filePath }) {
            currentSongs.add(song)
            allPlaylist[playlistName] = currentSongs
            PlaylistsManager.savePlaylists(context, allPlaylist)
        }
    }

    fun removeSongFromPlaylist(playlistName: String, song: Song) {
        val context = Locator.requireApplication
        val currentSongs = allPlaylist[playlistName]?.toMutableList() ?: return
        currentSongs.removeIf { it.filePath == song.filePath }
        allPlaylist[playlistName] = currentSongs
        PlaylistsManager.savePlaylists(context, allPlaylist)
    }
}
