package com.cbocka.soundzen.data.repository

import com.cbocka.soundzen.data.dao.SongDaoStorage
import com.cbocka.soundzen.data.model.Song
import java.io.File

class SongRepository private constructor() {
    private var allSongs = ArrayList<Song>()

    companion object  {
        val instance = SongRepository()
    }

    fun getAllSongs(directory: File) : ArrayList<Song> {
        if (allSongs.isEmpty())
            allSongs = SongDaoStorage.instance.findMP3Files(directory)

        return allSongs
    }
}