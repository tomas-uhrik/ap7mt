package com.example.audioplayer

data class Playlist (
    val name: String,
    val songs : MutableList<Data>
){
    fun addSong(song: Data) {
        songs.add(song)
    }
}

