package com.cbocka.soundzen.data.model

data class MusicDirectory(val name: String, val path: String) {

    companion object {
        const val KEY = "music_directory_key"
    }
}