import android.content.Context
import android.content.SharedPreferences
import com.example.audioplayer.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {

    private val PREFS_NAME = "playlist_prefs"
    private val gson = Gson()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun savePlaylists(playlists: List<Playlist>) {
        val json = gson.toJson(playlists)
        sharedPreferences.edit().putString("playlists", json).apply()
    }

    fun getPlaylists(): List<Playlist> {
        val json = sharedPreferences.getString("playlists", "")
        if (json.isNullOrEmpty()) {
            return emptyList()
        }

        val typeToken = object : TypeToken<List<Playlist>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    fun getPlaylistByName(name: String): Playlist? {
        val json = sharedPreferences.getString("playlists", "")
        if (json.isNullOrEmpty()) {
            return null
        }

        val typeToken = object : TypeToken<List<Playlist>>() {}.type
        val playlists = gson.fromJson<List<Playlist>>(json, typeToken)

        return playlists.find { it.name == name }
    }


}