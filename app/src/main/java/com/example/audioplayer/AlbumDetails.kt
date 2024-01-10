package com.example.audioplayer

import SharedPreferencesManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView

class AlbumDetails : AppCompatActivity() {

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var playlistName: TextView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var albumDetailsAdapter: AlbumDetailsAdapter
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)

        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        myRecyclerView = findViewById(R.id.recyclerView)
        playlistName = findViewById(R.id.playlistName)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        val playlistNameString = intent.getStringExtra("playlistName").toString()
        playlistName.text = playlistNameString


        // Zde získáte konkrétní playlist pomocí názvu ze SharedPreferences
        val playlist = sharedPreferencesManager.getPlaylistByName(playlistNameString)

        if (playlist != null){
            // Inicializace adaptéru pro skladby
            albumDetailsAdapter = AlbumDetailsAdapter(this, playlist)

            // Nastavení LayoutManageru a adaptéru pro RecyclerView
            myRecyclerView.layoutManager = LinearLayoutManager(this)
            myRecyclerView.adapter = albumDetailsAdapter
        }




    }
}