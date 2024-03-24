package com.cbocka.soundzen.data.model

import java.io.File

data class Song(val songName : String, val artist : String, var duration : String, val mp3Name : String, val file : File) {
}