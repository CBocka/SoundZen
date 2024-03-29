package com.cbocka.soundzen.ui.downloadmusic.yt.usecase

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.data.model.SongMP3
import com.cbocka.soundzen.data.repository.SongMP3Repository
import com.cbocka.soundzen.utils.Utils
import kotlinx.coroutines.launch

class DownloadMusicYTViewModel : ViewModel() {

    lateinit var videoId: String

    private val state = MutableLiveData<DownloadMusicYTState>()

    fun getState(): MutableLiveData<DownloadMusicYTState> {
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
                TextUtils.isEmpty(url.value) -> state.value = DownloadMusicYTState.UrlIsMandatory
                TextUtils.isEmpty(artistName.value) -> state.value =
                    DownloadMusicYTState.ArtistIsMandatory
                TextUtils.isEmpty(songName.value) -> state.value =
                    DownloadMusicYTState.SongNameIsMandatory
                videoId == "" -> state.value = DownloadMusicYTState.UrlNotValid
                else -> {
                    state.value = DownloadMusicYTState.Loading(true)

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

        state.value = DownloadMusicYTState.Success
    }

    inner class RequestBase64(private val downloadMusicState: MutableLiveData<DownloadMusicYTState>) :
        Thread() {
        override fun run() {
            super.run()
            songMp3 = getMP3FromYTLink(url.value.toString())

            downloadMusicState.postValue(DownloadMusicYTState.Loading(false))
            sleep(300)

            downloadMusicState.postValue(DownloadMusicYTState.SongOK)
        }
    }

    private fun getMP3FromYTLink(ytLink: String): SongMP3 {
        return SongMP3Repository.instance.getMP3FromYTLink(ytLink)
    }

    fun resetState() {
        state.value = DownloadMusicYTState.Completed
    }
}