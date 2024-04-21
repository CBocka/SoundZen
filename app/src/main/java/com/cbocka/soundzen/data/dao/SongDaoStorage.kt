package com.cbocka.soundzen.data.dao

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import com.cbocka.soundzen.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

class SongDaoStorage private constructor() {

    companion object  {
        val instance = SongDaoStorage()
    }

    fun findMP3Files(directory: File): ArrayList<Song> {
        val mp3Files = ArrayList<Song>()
        val files = directory.listFiles()

        files?.let {
            for (file in it) {
                if (file.isDirectory) {
                    // if is a directory, look recursively in that directories
                    mp3Files.addAll(findMP3Files(file))
                } else {
                    // if is a file, verify if is a mp3 file
                    if (file.name.lowercase().endsWith(".mp3")) {

                        val songName : String
                        val artist : String

                        if (file.name.contains("--")) {
                            artist = file.name.split("--")[0].trim()
                            songName = file.name.split("--")[1].dropLast(4).trim()
                        }
                        else {
                            artist = Song.DEFAULT_ARTIST
                            songName = file.name.dropLast(4).trim()
                        }

                        val mp3Name = file.name
                        val song = Song(songName, artist, "", mp3Name, file.absolutePath)

                        mp3Files.add(song)
                    }
                }
            }
        }

        return mp3Files
    }

    fun deleteSong(song: Song) : Boolean {

        val file = File(song.filePath)

        if (file.exists())
            file.delete()

        return true
    }
}