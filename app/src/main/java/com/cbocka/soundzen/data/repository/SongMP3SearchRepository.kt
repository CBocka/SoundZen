package com.cbocka.soundzen.data.repository

import com.cbocka.soundzen.data.dao.SongMP3SearchDao
import com.cbocka.soundzen.data.model.SongMP3Search

class SongMP3SearchRepository private constructor() {

    companion object  {
        val instance = SongMP3SearchRepository()
    }

    fun getMP3FromSongName(songName: String) : SongMP3Search? {
        val songMP3Search = SongMP3SearchDao.instance.getMP3FromSongName(songName)

        return if (songMP3Search.isNullOrEmpty())
            null
        else
            songMP3Search[0]
    }

    fun downloadSong(outputPath: String, songUrl: String) {
        SongMP3SearchDao.instance.downloadSong(outputPath, songUrl)
    }
}