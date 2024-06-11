package com.cbocka.soundzen.data.dao

import com.cbocka.soundzen.data.model.MusicDirectory
import com.cbocka.soundzen.data.model.Song
import java.io.File

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
                    mp3Files.addAll(findMP3Files(file))
                } else {
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

    fun findDirectories(directory: File): ArrayList<MusicDirectory> {
        val directories = ArrayList<MusicDirectory>()
        val files = directory.listFiles()

        files?.let {
            for (file in it) {
                if (file.isDirectory && !file.name.startsWith(".")) {
                    if (containsMp3FilesRecursively(file)) {
                        val musicDirectory = MusicDirectory(file.name, file.absolutePath)
                        directories.add(musicDirectory)
                    }
                    directories.addAll(findDirectories(file))
                }
            }
        }

        return directories
    }

    private fun containsMp3FilesRecursively(directory: File): Boolean {
        val files = directory.listFiles() ?: return false
        for (file in files) {
            if (file.isFile && file.extension.equals("mp3", ignoreCase = true)) {
                return true
            } else if (file.isDirectory) {
                if (containsMp3FilesRecursively(file)) {
                    return true
                }
            }
        }
        return false
    }

    fun deleteSong(song: Song) : Boolean {

        val file = File(song.filePath)

        if (file.exists())
            file.delete()

        return true
    }
}



