package com.example.audioplayer

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class PlayerActivity : AppCompatActivity() {
    private lateinit var song_name: TextView
    private lateinit var artist_name: TextView
    private lateinit var duration_played: TextView
    private lateinit var duration_total: TextView
    private lateinit var cover_art: ImageView
    private lateinit var nextBtn: ImageView
    private lateinit var prevBtn: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var shuffleBtn: ImageView
    private lateinit var repeatBtn: ImageView
    private lateinit var playPauseBtn: FloatingActionButton
    private lateinit var seekBar: SeekBar
    private val mediaPlayer: MediaPlayer by lazy { MediaPlayer() }
    private lateinit var dataList : List<Data>
    private lateinit var data: Data
    private lateinit var playThread: Thread
    private lateinit var nextThread: Thread
    private lateinit var prevThread: Thread
    private lateinit var handler: Handler
    private var position: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initViews()

        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            // Přechod zpět do MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



        val dataJson = intent.getStringExtra("data")
        if (dataJson != null) {
            val gson = Gson()
            val listType = object : TypeToken<List<Data>>() {}.type
            dataList = gson.fromJson(dataJson, listType)
            position = intent.getIntExtra("position", -1)
            data = dataList[position]
        }

        handler = Handler()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.seekTo(progress * 1000)
                    } else {
                        // Pokud je hudba zastavená, uložte pozici pro pozdější spuštění
                        mediaPlayer.seekTo(progress * 1000)
                        mediaPlayer.pause()
                    }
                    updateSeekBar()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        getIntentMethod()

        val updateSeekBar = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val mCurrentPosition = mediaPlayer.currentPosition / 1000
                    seekBar.progress = mCurrentPosition
                    duration_played.text = formattedTime(mCurrentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        }

        runOnUiThread(updateSeekBar)
    }

    override fun onResume() {
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()
    }

    private fun prevThreadBtn() {
        prevThread = object : Thread() {
            override fun run() {
                super.run()
                prevBtn.setOnClickListener {
                    prevBtnClicked()
                }
            }
        }
        prevThread.start()
    }

    private fun prevBtnClicked() {
        stopAndReleaseMediaPlayer()
        position = if (position - 1 < 0) dataList.size - 1 else position - 1
        data = dataList[position]
        mediaPlayer.setDataSource(applicationContext, Uri.parse(data.preview))
        mediaPlayer.prepare()
        loadData(data)
        seekBar.max = mediaPlayer.duration / 1000

        mediaPlayer.start()

        if (mediaPlayer.isPlaying) {
            playPauseBtn.setImageResource(R.drawable.baseline_pause)
        } else {
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow)
        }
    }

    private fun nextThreadBtn() {
        nextThread = object : Thread() {
            override fun run() {
                super.run()
                nextBtn.setOnClickListener {
                    nextBtnClicked()
                }
            }
        }
        nextThread.start()
    }

    private fun nextBtnClicked() {
        stopAndReleaseMediaPlayer()
        position = (position + 1) % dataList.size
        data = dataList[position]
        mediaPlayer.setDataSource(applicationContext, Uri.parse(data.preview))
        mediaPlayer.prepare()
        loadData(data)
        seekBar.max = mediaPlayer.duration / 1000

        mediaPlayer.start()

        if (mediaPlayer.isPlaying) {
            playPauseBtn.setImageResource(R.drawable.baseline_pause)
        } else {
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow)
        }
    }

    private fun playThreadBtn() {
        playThread = object : Thread() {
            override fun run() {
                super.run()
                playPauseBtn.setOnClickListener {
                    playPauseBtnClicked()
                }
            }
        }
        playThread.start()  // Spustit playThread
    }

    private fun playPauseBtnClicked() {
        if (mediaPlayer.isPlaying) {
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow)
            mediaPlayer.pause()
        } else {
            playPauseBtn.setImageResource(R.drawable.baseline_pause)
            mediaPlayer.start()
        }


    }

    private fun formattedTime(mCurrentPosition: Int): String {
        var totalout = ""
        var totalNew = ""
        val seconds = (mCurrentPosition % 60).toString()
        val minutes = (mCurrentPosition / 60).toString()

        totalout = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"

        return if (seconds.length == 1) {
            totalNew
        } else {
            totalout
        }
    }

    private fun getIntentMethod() {
        loadData(data)
        stopAndReleaseMediaPlayer()
        mediaPlayer.setDataSource(applicationContext, Uri.parse(data.preview))
        mediaPlayer.prepare()
        seekBar.max = mediaPlayer.duration / 1000
    }

    private fun initViews() {
        song_name = findViewById(R.id.song_name)
        artist_name = findViewById(R.id.song_artist)
        duration_played = findViewById(R.id.durationPlayed)
        duration_total = findViewById(R.id.durationTotal)
        cover_art = findViewById(R.id.cover_art)
        nextBtn = findViewById(R.id.id_next)
        prevBtn = findViewById(R.id.id_prev)
        backBtn = findViewById(R.id.back_btn)
        shuffleBtn = findViewById(R.id.id_shuffle)
        repeatBtn = findViewById(R.id.id_repeat)
        playPauseBtn = findViewById(R.id.play_pause)
        seekBar = findViewById(R.id.seekBar)
    }

    private fun loadData(data: Data){
        duration_total.text = formattedTime(30)
        Picasso.get().load(data.album.cover_medium).into(findViewById<ImageView>(R.id.cover_art))
        song_name.text = data.title
        artist_name.text = data.artist.name
        duration_played.text = formattedTime(0)
    }

    private fun stopAndReleaseMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
    }



    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseMediaPlayer()
    }


    private fun updateSeekBar() {
        val updateSeekBar = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val mCurrentPosition = mediaPlayer.currentPosition / 1000
                    seekBar.progress = mCurrentPosition
                    duration_played.text = formattedTime(mCurrentPosition)

                    // Pokud skladba dohraje do konce, přehrajte od začátku a změňte tlačítko na play
                    if (mCurrentPosition >= seekBar.max) {
                        seekBar.progress = 0
                        duration_played.text = formattedTime(0)  // Aktualizujte hodnotu času na začátek
                        playPauseBtn.setImageResource(R.drawable.baseline_play_arrow)
                        mediaPlayer.pause()
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }

        runOnUiThread(updateSeekBar)
    }

}