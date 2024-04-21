package com.cbocka.soundzen.data.repository

import com.cbocka.soundzen.data.dao.SongDaoStorage
import com.cbocka.soundzen.data.model.MusicDirectory
import com.cbocka.soundzen.data.model.Song
import java.io.File

class SongRepository private constructor() {
    var allSongs = ArrayList<Song>()
    var allDirectories = ArrayList<MusicDirectory>()

    companion object  {
        val instance = SongRepository()
    }

    fun getAllSongs(directory: File) : ArrayList<Song> {
        allSongs = SongDaoStorage.instance.findMP3Files(directory)

        return allSongs
    }

    fun getAllDirectories(directory: File) : ArrayList<MusicDirectory> {
        allDirectories = SongDaoStorage.instance.findDirectories(directory)

        return allDirectories
    }

    fun deleteSong(song: Song) : Boolean {
        return SongDaoStorage.instance.deleteSong(song)
    }
}