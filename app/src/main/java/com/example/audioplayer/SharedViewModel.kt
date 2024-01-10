package com.example.audioplayer

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val playlistItems: MutableList<Playlist> = mutableListOf()
    fun addSongToPlaylist(song: Data, playlistName: String) {
        val playlist = playlistItems.find { it.name == playlistName }
        playlist?.addSong(song)
    }
}