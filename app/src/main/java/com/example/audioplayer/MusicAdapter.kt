package com.example.audioplayer
import SharedPreferencesManager
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso


class MusicAdapter(
    private val context: Context,
    private val dataList:List<Data>,
    private val sharedViewModel: SharedViewModel):


    RecyclerView.Adapter<MusicAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentData = dataList[position]
        holder.title.text = currentData.title
        Picasso.get().load(currentData.album.cover).into(holder.image);

        holder.itemView.setOnClickListener {
            val gson = Gson()
            val dataJson = gson.toJson(dataList)
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("data", dataJson)
            intent.putExtra("position", position)
            context.startActivity(intent)
        }

        holder.menuMore.setOnClickListener(){
            val popupMenu = PopupMenu(context, holder.menuMore)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.popup, popupMenu.menu)
            // Přidání položek playlistů do PopupMenu
            val playlists = sharedViewModel.playlistItems
            for (playlist in playlists) {
                popupMenu.menu.add(Menu.NONE, Menu.NONE, Menu.NONE, playlist.name).setOnMenuItemClickListener {
                    sharedViewModel.addSongToPlaylist(currentData, playlist.name)
                    val updatedPlaylists = sharedViewModel.playlistItems
                    val sharedPreferencesManager = SharedPreferencesManager(context)
                    sharedPreferencesManager.savePlaylists(updatedPlaylists)
                    true
                }
            }

            popupMenu.show()
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView
        val title: TextView
        val menuMore: ImageView
        init {
            image = itemView.findViewById(R.id.music_img)
            title = itemView.findViewById(R.id.music_title)
            menuMore = itemView.findViewById(R.id.menuMore)
        }
    }


}


