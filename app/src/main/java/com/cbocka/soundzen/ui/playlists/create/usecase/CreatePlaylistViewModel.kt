package com.cbocka.soundzen.ui.playlists.create.usecase

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbocka.soundzen.data.model.Playlist
import com.cbocka.soundzen.data.repository.PlaylistRepository
import com.cbocka.soundzen.utils.Locator
import com.cbocka.soundzen.utils.PlaylistsManager
import kotlinx.coroutines.launch

class CreatePlaylistViewModel: ViewModel() {

    private val state = MutableLiveData<CreatePlaylistState>()

    fun getState(): MutableLiveData<CreatePlaylistState> {
        return state
    }

    var name = MutableLiveData<String>()

    fun validatePlaylist() {
        viewModelScope.launch {
            when {
                TextUtils.isEmpty(name.value) -> state.value = CreatePlaylistState.NameIsMandatoryError
                else -> {
                    when {
                        PlaylistsManager.exists(Locator.requireApplication, name.value.toString()) -> state.value = CreatePlaylistState.PlaylistAlreadyExistsError
                        else -> {
                            addPlaylist(name.value.toString().trim())
                            state.value = CreatePlaylistState.Success}
                    }
                }
            }
        }
    }

    private fun addPlaylist(playlist: String) {
        PlaylistRepository.instance.addPlaylist(playlist)
    }
}