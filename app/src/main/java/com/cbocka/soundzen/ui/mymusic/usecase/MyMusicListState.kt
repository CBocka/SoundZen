package com.cbocka.soundzen.ui.mymusic.usecase

sealed class MyMusicListState {
    data object NoData : MyMusicListState()
    data class Loading(val show : Boolean) : MyMusicListState()
    data object Success : MyMusicListState()
}