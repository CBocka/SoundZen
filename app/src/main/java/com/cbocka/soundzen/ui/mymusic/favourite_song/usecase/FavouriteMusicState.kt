package com.cbocka.soundzen.ui.mymusic.favourite_song.usecase

import com.cbocka.soundzen.ui.mymusic.all_music.usecase.MyMusicListState

sealed class FavouriteMusicState {
    data object NoData : FavouriteMusicState()
    data object Success : FavouriteMusicState()
    data object Completed : FavouriteMusicState()
}
