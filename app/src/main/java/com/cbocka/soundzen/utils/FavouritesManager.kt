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

class FavoritesManager private constructor() {

    companion object {
        private const val FILE_NAME = ".favorites.json"

        private fun getFile(context: Context): File {
            return File(context.filesDir, FILE_NAME).apply {
                if (!exists()) {
                    createNewFile()
                    FileWriter(this).use { writer ->
                        writer.write("[]")
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

        fun saveFavorites(context: Context, favorites: List<Song>) {
            val jsonArray = JSONArray()
            for (favorite in favorites) {
                jsonArray.put(songToJson(favorite))
            }

            try {
                FileWriter(getFile(context)).use { writer ->
                    writer.write(jsonArray.toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun loadFavorites(context: Context): ArrayList<Song> {
            val favorites = arrayListOf<Song>()

            try {
                BufferedReader(FileReader(getFile(context))).use { reader ->
                    val content = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        content.append(line)
                    }

                    val jsonArray = JSONArray(content.toString())
                    for (i in 0 until jsonArray.length()) {
                        val songJson = jsonArray.getJSONObject(i)
                        favorites.add(jsonToSong(songJson))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return favorites
        }

        fun addFavorite(context: Context, song: Song) {
            val favorites = loadFavorites(context).toMutableList()
            if (favorites.none { it.filePath == song.filePath }) {
                favorites.add(song)
                saveFavorites(context, favorites)
            }
        }

        fun removeFavorite(context: Context, song: Song) {
            val favorites = loadFavorites(context).toMutableList()
            val iterator = favorites.iterator()
            while (iterator.hasNext()) {
                val currentSong = iterator.next()
                if (currentSong.filePath == song.filePath) {
                    iterator.remove()
                }
            }
            saveFavorites(context, favorites)
        }

        fun isFavorite(context: Context, song: Song): Boolean {
            val favorites = loadFavorites(context)
            return favorites.any { it.filePath == song.filePath }
        }
    }
}
