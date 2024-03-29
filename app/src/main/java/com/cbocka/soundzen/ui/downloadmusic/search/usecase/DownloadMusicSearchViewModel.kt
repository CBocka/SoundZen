package com.cbocka.soundzen.ui.downloadmusic.search.usecase

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.SongMP3Search
import com.cbocka.soundzen.data.repository.SongMP3SearchRepository
import kotlinx.coroutines.launch

class DownloadMusicSearchViewModel : ViewModel() {

    private val state = MutableLiveData<DownloadMusicSearchState>()

    fun getState(): MutableLiveData<DownloadMusicSearchState> {
        return state
    }

    var artistName = MutableLiveData<String>("")
    var songName = MutableLiveData<String>("")

    var songMP3Search : SongMP3Search? = null

    fun validateSong(context: Context) {
        viewModelScope.launch {
            when {
                TextUtils.isEmpty(artistName.value) -> state.value = DownloadMusicSearchState.ArtistIsMandatory
                TextUtils.isEmpty(songName.value) -> state.value = DownloadMusicSearchState.SongNameIsMandatory
                else -> {
                    state.value = DownloadMusicSearchState.Loading(true,
                        context.getString(R.string.searching_loading_title))

                    RequestMp3(state).start()
                }
            }
        }
    }

    private fun getMP3FromSongName(search: String): SongMP3Search? {
        return SongMP3SearchRepository.instance.getMP3FromSongName(search)
    }

    fun downloadSong(downloadPath: String, context: Context) {
        state.value = DownloadMusicSearchState.Loading(true,
            context.getString(R.string.download_loading_title))

        DownloadMp3(
            state,
            "$downloadPath/${artistName.value.toString().trim()} -- ${songName.value.toString().trim()}.mp3"
            , songMP3Search!!.url
        ).execute()
    }

    inner class RequestMp3(private val downloadMusicState: MutableLiveData<DownloadMusicSearchState>) :
        Thread() {
        override fun run() {
            super.run()
            songMP3Search = getMP3FromSongName("${songName.value} ${artistName.value}")

            downloadMusicState.postValue(DownloadMusicSearchState.Loading(false, ""))
            sleep(300)

            if (songMP3Search == null)
                downloadMusicState.postValue(DownloadMusicSearchState.SongNotOK)
            else
                downloadMusicState.postValue(DownloadMusicSearchState.SongOK)
        }
    }

    inner class DownloadMp3(private val downloadMusicState: MutableLiveData<DownloadMusicSearchState>,
                            private val outputPath: String, private val songUrl: String) :
        AsyncTask<String, Void, Unit>() {

        override fun doInBackground(vararg urls: String) {
            SongMP3SearchRepository.instance.downloadSong(outputPath, songUrl)

            downloadMusicState.postValue(DownloadMusicSearchState.Loading(false, ""))
            Thread.sleep(300)

            downloadMusicState.postValue(DownloadMusicSearchState.Success)
        }
    }

    fun resetState() {
        state.value = DownloadMusicSearchState.Completed
    }
}