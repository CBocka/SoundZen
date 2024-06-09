package com.cbocka.soundzen.ui.mymusic.favourite_song.usecase

sealed class FavouriteMusicState {
    data object NoData : FavouriteMusicState()
    data object Success : FavouriteMusicState()
    data object Completed : FavouriteMusicState()
}
