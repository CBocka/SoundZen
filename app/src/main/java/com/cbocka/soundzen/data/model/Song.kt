package com.cbocka.soundzen.data.model

import android.os.Parcel
import android.os.Parcelable

data class Song(
    val songName: String,
    val artist: String,
    var duration: String,
    val mp3Name: String,
    val filePath: String,
    var isFavorite: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(songName)
        dest.writeString(artist)
        dest.writeString(duration)
        dest.writeString(mp3Name)
        dest.writeString(filePath)
    }

    companion object CREATOR : Parcelable.Creator<Song> {

        const val DEFAULT_ARTIST : String = "< Undefined >"

        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
