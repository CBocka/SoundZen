package com.cbocka.soundzen.data.dao

import android.media.MediaPlayer
import com.cbocka.soundzen.data.model.Song
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
                    if (file.name.toLowerCase().endsWith(".mp3")) {

                        val songName : String
                        val artist : String

                        if (file.name.contains("--")) {
                            artist = file.name.split("--")[0].trim()
                            songName = file.name.split("--")[1].dropLast(4).trim()
                        }
                        else {
                            artist = "< Undefined >"
                            songName = file.name.dropLast(4).trim()
                        }

                        val duration = getDuration(file)
                        val mp3Name = file.name

                        mp3Files.add(Song(songName, artist, duration, mp3Name))
                    }
                }
            }
        }

        return mp3Files
    }

    private fun getDuration(file: File): String {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(file.path)
        mediaPlayer.prepare()
        val durationInMillis = mediaPlayer.duration
        //mediaPlayer.release()

        // convert duration song to mm:ss
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}