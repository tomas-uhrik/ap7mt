package com.example.audioplayer


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SongsFragment : Fragment() {

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var searchView: SearchView
    private val apiInterface: ApiInterface = getApiInterface()
    private lateinit var sharedViewModel : SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_songs, container, false)
        myRecyclerView = view.findViewById(R.id.recyclerView)
        myRecyclerView.setHasFixedSize(true)

        setHasOptionsMenu(true)

        // Defaultní hodnota pro vyhledávání
        fetchData("ektor")

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchItem = menu.findItem(R.id.search_option)
        searchView = searchItem.actionView as SearchView

        // Nastavení posluchače pro změny v SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fetchData(newText.orEmpty())
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun fetchData(query: String) {

        val retrofitData = apiInterface.getData(query)

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                if (response.isSuccessful) {
                    val dataList = response.body()?.data

                    if (dataList != null) {
                        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
                        musicAdapter = MusicAdapter(requireContext(), dataList, sharedViewModel)
                        myRecyclerView.adapter = musicAdapter
                        myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    } else {
                        // Zpracování situace, kdy dataList je null (prázdný vstup ve vyhledávacím baru)
                        // Můžete například zobrazit zprávu uživateli nebo provést jinou akci.
                        Log.d("SongsFragment", "DataList je null. Prázdný vstup ve vyhledávacím baru?")
                    }
                }
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Log.d("SongsFragment", "Chyba při žádosti: ${t.message}")
            }
        })
    }

    private fun getApiInterface(): ApiInterface {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiInterface::class.java)
    }
}