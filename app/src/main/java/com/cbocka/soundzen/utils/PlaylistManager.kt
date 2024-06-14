package com.cbocka.soundzen.utils

import android.content.Context
import com.cbocka.soundzen.data.model.Song
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class PlaylistsManager private constructor() {

    companion object {
        private const val FILE_NAME = ".playlists.json"

        var allPlaylists = mutableMapOf<String, List<Song>>()

        private fun getFile(context: Context): File {
            return File(context.filesDir, FILE_NAME).apply {
                if (!exists()) {
                    createNewFile()
                    FileWriter(this).use { writer ->
                        writer.write("[]")  // Inicializa el fichero con un array JSON vacío
                    }
                }
            }
        }

        private fun songToJson(song: Song): JSONObject {
            return JSONObject().apply {
                put("songName", song.songName)
                put("artist", song.artist)
                put("duration", song.duration)
                put("mp3Name", song.mp3Name)
                put("filePath", song.filePath)
                put("isFavorite", song.isFavorite)
            }
        }

        private fun jsonToSong(json: JSONObject): Song {
            return Song(
                songName = json.getString("songName"),
                artist = json.getString("artist"),
                duration = json.getString("duration"),
                mp3Name = json.getString("mp3Name"),
                filePath = json.getString("filePath"),
                isFavorite = json.getBoolean("isFavorite")
            )
        }

        private fun playlistToJson(playlistName: String, songs: List<Song>): JSONObject {
            val jsonArray = JSONArray()
            for (song in songs) {
                jsonArray.put(songToJson(song))
            }
            return JSONObject().apply {
                put("playlistName", playlistName)
                put("songs", jsonArray)
            }
        }

        private fun jsonToPlaylist(json: JSONObject): Pair<String, List<Song>> {
            val playlistName = json.getString("playlistName")
            val songsJsonArray = json.getJSONArray("songs")
            val songs = mutableListOf<Song>()
            for (i in 0 until songsJsonArray.length()) {
                songs.add(jsonToSong(songsJsonArray.getJSONObject(i)))
            }
            return Pair(playlistName, songs)
        }

        fun savePlaylists(context: Context, playlists: Map<String, List<Song>>) {
            val jsonArray = JSONArray()
            for ((playlistName, songs) in playlists) {
                jsonArray.put(playlistToJson(playlistName, songs))
            }

            try {
                FileWriter(getFile(context)).use { writer ->
                    writer.write(jsonArray.toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun loadPlaylists(context: Context): MutableMap<String, List<Song>> {
            val playlists = mutableMapOf<String, List<Song>>()

            try {
                BufferedReader(FileReader(getFile(context))).use { reader ->
                    val content = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        content.append(line)
                    }

                    val jsonArray = JSONArray(content.toString())
                    for (i in 0 until jsonArray.length()) {
                        val playlistJson = jsonArray.getJSONObject(i)
                        val (playlistName, songs) = jsonToPlaylist(playlistJson)
                        playlists[playlistName] = songs
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            allPlaylists = playlists

            return playlists
        }

        fun addPlaylist(context: Context, playlistName: String, songs: List<Song> = mutableListOf()) {
            val playlists = loadPlaylists(context)
            if (!playlists.containsKey(playlistName)) {
                playlists[playlistName] = songs
                savePlaylists(context, playlists)
            }
        }

        fun getSongsFromPlaylist(context: Context, playlistName: String): List<Song>? {
            val playlists = loadPlaylists(context)
            return playlists[playlistName]
        }

        fun exists(context: Context, name: String): Boolean {
            val playlists = loadPlaylists(context)

            for (p in playlists) {
                if (p.key == name)
                    return true
            }

            return false
        }
    }
}
