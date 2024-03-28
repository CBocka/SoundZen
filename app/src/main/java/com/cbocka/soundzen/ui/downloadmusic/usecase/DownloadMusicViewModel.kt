package com.cbocka.soundzen.ui.downloadmusic.usecase

import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.model.SongMP3
import com.cbocka.soundzen.data.repository.SongMP3Repository
import com.cbocka.soundzen.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.io.File
import java.util.concurrent.TimeUnit

class DownloadMusicViewModel : ViewModel() {

    lateinit var videoId: String

    private val state = MutableLiveData<DownloadMusicState>()

    fun getState(): MutableLiveData<DownloadMusicState> {
        return state
    }

    var url = MutableLiveData<String>("")
    var artistName = MutableLiveData<String>("")
    var songName = MutableLiveData<String>("")

    private lateinit var songMp3: SongMP3

    fun getVideoId(url: String) {
        videoId =
            if (url.indexOf("watch?v=") != -1)
                url.substringAfter("watch?v=")
            else {
                val startIndex = url.lastIndexOf("/") + 1
                val endIndex = url.indexOf("?")

                if (startIndex >= 0 && endIndex >= 0 && startIndex < endIndex) {
                    url.substring(startIndex, endIndex)
                } else {
                    ""
                }
            }
    }

    fun validateSong() {
        viewModelScope.launch {
            when {
                TextUtils.isEmpty(url.value) -> state.value = DownloadMusicState.UrlIsMandatory
                TextUtils.isEmpty(artistName.value) -> state.value = DownloadMusicState.ArtistIsMandatory
                TextUtils.isEmpty(songName.value) -> state.value = DownloadMusicState.SongNameIsMandatory
                videoId == "" -> state.value = DownloadMusicState.UrlNotValid
                else -> {
                    state.value = DownloadMusicState.Loading(true)

                    RequestBase64(state).start()
                }
            }
        }
    }

    fun downloadSong(downloadPath: String) {
        val decodedBytes = Utils.base64ToMp3(songMp3.mp3Base64)

        SongMP3Repository.instance.downloadSong(
            decodedBytes,
            "$downloadPath/${artistName.value.toString().trim()} -- ${songName.value.toString().trim()}.mp3"
        )

        state.value = DownloadMusicState.Success
    }

    inner class RequestBase64(private val downloadMusicState: MutableLiveData<DownloadMusicState>) :
        Thread() {
        override fun run() {
            super.run()
            songMp3 = getMP3FromYTLink(url.value.toString())

            downloadMusicState.postValue(DownloadMusicState.Loading(false))
            sleep(300)

            downloadMusicState.postValue(DownloadMusicState.SongOK)
        }
    }

    private fun getMP3FromYTLink(ytLink: String): SongMP3 {
        return SongMP3Repository.instance.getMP3FromYTLink(ytLink)
    }
}