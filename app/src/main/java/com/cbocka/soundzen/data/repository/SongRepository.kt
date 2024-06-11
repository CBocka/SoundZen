package com.cbocka.soundzen.data.repository

import com.cbocka.soundzen.data.dao.SongDaoStorage
import com.cbocka.soundzen.data.model.MusicDirectory
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.utils.FavoritesManager
import com.cbocka.soundzen.utils.Locator
import java.io.File

class SongRepository private constructor() {
    var allSongs = ArrayList<Song>()
    private var allDirectories = ArrayList<MusicDirectory>()
    var songFromADirectory = ArrayList<Song>()

    companion object  {
        val instance = SongRepository()
    }

    fun getAllSongs(directory: File) : ArrayList<Song> {
        allSongs = SongDaoStorage.instance.findMP3Files(directory)

        return allSongs
    }

    fun getSongsFromDirectory(directory: File) : ArrayList<Song> {
        songFromADirectory = SongDaoStorage.instance.findMP3Files(directory)

        return SongDaoStorage.instance.findMP3Files(directory)
    }

    fun getAllDirectories(directory: File) : ArrayList<MusicDirectory> {
        allDirectories = SongDaoStorage.instance.findDirectories(directory)

        return allDirectories
    }

    fun deleteSong(song: Song) : Boolean {
        return SongDaoStorage.instance.deleteSong(song)
    }

    fun getFavouriteSongs(): ArrayList<Song> {
        return FavoritesManager.loadFavorites(Locator.requireApplication)
    }
}
