package com.example.audioplayer


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class AlbumDetailsAdapter(private val context: Context, private val playlist: Playlist) :
    RecyclerView.Adapter<AlbumDetailsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return playlist.songs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = playlist.songs[position]
        holder.title.text = song.title
        Picasso.get().load(song.album.cover).into(holder.image);


        holder.itemView.setOnClickListener {
            // Akce po kliknutí na skladbu
        }


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView
        val title: TextView
        init {
            image = itemView.findViewById(R.id.music_img)
            title = itemView.findViewById(R.id.music_title)


        }
    }



}