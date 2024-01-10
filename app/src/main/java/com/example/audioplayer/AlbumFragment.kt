package com.example.audioplayer

import SharedPreferencesManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AlbumFragment : Fragment() {

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var playlistViewModel: SharedViewModel
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var editTextPlaylist: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)

        // Inicializace SharedPreferencesManager
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        playlistViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        // Načtěte playlisty z SharedPreferences
        playlistViewModel.playlistItems.addAll(sharedPreferencesManager.getPlaylists())

        // Inicializace PlaylistViewModel
        playlistViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        myRecyclerView = view.findViewById(R.id.recyclerView)
        myRecyclerView.setHasFixedSize(true)

        // Přiřaď odkaz na EditText
        editTextPlaylist = view.findViewById(R.id.editTextPlaylist)

        setHasOptionsMenu(true)

        // Přidej onClickListener pro tlačítko
        view.findViewById<Button>(R.id.buttonCreatePlaylist).setOnClickListener {
            createPlaylist()
        }

        // Inicializace RecyclerView a Adapteru
        albumAdapter = AlbumAdapter(requireContext(), playlistViewModel)
        myRecyclerView.adapter = albumAdapter
        myRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        return view
    }

    private fun createPlaylist() {
        val playlistName = editTextPlaylist.text.toString().trim()

        if (playlistName.isNotEmpty()) {
            // Vytvoř nový playlist
            val newPlaylist = Playlist(playlistName, mutableListOf())

            // Přidej nový playlist do seznamu v PlaylistViewModel
            playlistViewModel.playlistItems.add(newPlaylist)

            // Přidej nový playlist do seznamu
            val playlists = sharedPreferencesManager.getPlaylists().toMutableList()
            playlists.add(newPlaylist)
            sharedPreferencesManager.savePlaylists(playlists)

            // Aktualizuj RecyclerView
            albumAdapter.notifyDataSetChanged()

            // Vyčisti EditText
            editTextPlaylist.text.clear()
        } else {
            Toast.makeText(requireContext(), "Enter a valid playlist name", Toast.LENGTH_SHORT).show()
        }
    }
}