package com.cbocka.soundzen.data.dao

import com.cbocka.soundzen.data.model.SongMP3Search
import com.cbocka.soundzen.utils.HttpUtil
import com.cbocka.soundzen.utils.Utils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class SongMP3SearchDao private constructor() {

    companion object  {
        val instance = SongMP3SearchDao()
    }

    fun getMP3FromSongName(songName: String) : List<SongMP3Search>? {
        val httpRequest = "https://spotify81.p.rapidapi.com/download_track?q=" +
                Utils.encodeURIComponent(songName) +
                "&onlyLinks=1"

        return HttpUtil.getInstance()!!.getResponseDataDownloadSearch(httpRequest, Array<SongMP3Search>::class.java)
    }

    fun downloadSong(outputPath: String, songUrl: String) {
        val url = songUrl
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response: Response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return
            }

            val inputStream: InputStream = response.body!!.byteStream()
            val outputStream: OutputStream = FileOutputStream(outputPath)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}