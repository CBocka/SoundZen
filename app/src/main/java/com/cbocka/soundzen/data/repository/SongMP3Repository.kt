package com.cbocka.soundzen.data.repository

import com.cbocka.soundzen.data.dao.SongMP3Dao
import com.cbocka.soundzen.data.model.SongMP3

class SongMP3Repository private constructor() {

    companion object  {
        val instance = SongMP3Repository()
    }

    fun getMP3FromYTLink(ytLink: String) : SongMP3 {
        return SongMP3Dao.instance.getMP3FromYTLink(ytLink)
    }

    fun downloadSong(decodedBytes: ByteArray, outputFilePath: String) {
        return SongMP3Dao.instance.downloadSong(decodedBytes, outputFilePath)
    }
}