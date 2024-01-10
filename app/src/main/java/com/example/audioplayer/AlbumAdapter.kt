package com.example.audioplayer


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class AlbumAdapter(private val context: Context, private val sharedViewModel: SharedViewModel) :
    RecyclerView.Adapter<AlbumAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.album_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return sharedViewModel.playlistItems.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val playlistName = sharedViewModel.playlistItems[position]

        holder.title.text = playlistName.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AlbumDetails::class.java)
            intent.putExtra("playlistName", holder.title.text)
            context.startActivity(intent)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.playlist_name)

    }
}