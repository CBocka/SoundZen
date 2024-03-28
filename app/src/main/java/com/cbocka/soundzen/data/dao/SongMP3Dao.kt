package com.cbocka.soundzen.data.dao

import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.model.SongMP3
import com.cbocka.soundzen.utils.HttpUtil
import com.cbocka.soundzen.utils.Utils
import java.io.File
import java.io.FileOutputStream

class SongMP3Dao private constructor() {

    companion object  {
        val instance = SongMP3Dao()
    }

    fun getMP3FromYTLink(ytLink: String) : SongMP3 {
        val httpRequest = "http://143.47.41.234:4167/download/" + Utils.encodeURIComponent(ytLink)

        return HttpUtil.getInstance()!!.getResponseData(httpRequest, SongMP3::class.java)!!
    }

    fun downloadSong(decodedBytes: ByteArray, outputFilePath: String) {
        try {
            val outputFile = File(outputFilePath)

            val outputStream = FileOutputStream(outputFile)
            outputStream.write(decodedBytes)

            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}