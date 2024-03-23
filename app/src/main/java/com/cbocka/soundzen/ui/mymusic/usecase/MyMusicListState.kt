package com.cbocka.soundzen.ui.mymusic.usecase

sealed class MyMusicListState {
    data object NoData : MyMusicListState()
    data object Success : MyMusicListState()
}