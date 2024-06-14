package com.cbocka.soundzen.ui.playlists.create.usecase

sealed class CreatePlaylistState {
    data object NameIsMandatoryError: CreatePlaylistState()
    data object PlaylistAlreadyExistsError: CreatePlaylistState()
    data object Success: CreatePlaylistState()
}
