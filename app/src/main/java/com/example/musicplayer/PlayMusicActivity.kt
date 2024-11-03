package com.example.musicplayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class PlayMusicActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        val song: Data? = intent.getParcelableExtra("song")
        if (song == null) {
            Log.e("PlayMusicActivity", "Error: Song data is null")
            finish() // Close activity if data is missing
            return
        }

        setupUI(song)
        setupMediaPlayer(song)
    }

    private fun setupUI(song: Data) {
        findViewById<TextView>(R.id.songTitle).text = song.title
        findViewById<TextView>(R.id.artistName).text = song.artist.name

        Picasso.get()
            .load(song.album.cover_big)
            .into(findViewById<ImageView>(R.id.albumArt))

        findViewById<ImageButton>(R.id.btnPlay).setOnClickListener { playMusic() }
        findViewById<ImageButton>(R.id.btnPause).setOnClickListener { pauseMusic() }

        seekBar = findViewById(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupMediaPlayer(song: Data) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(song.preview))
        seekBar.max = mediaPlayer.duration

        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentPosition
            handler.postDelayed(runnable, 1000)
        }

        handler.postDelayed(runnable, 1000)

        mediaPlayer.setOnCompletionListener {
            seekBar.progress = 0 // Reset seek bar when playback is complete
        }
    }

    private fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            handler.postDelayed(runnable, 1000) // Start updating SeekBar
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            handler.removeCallbacks(runnable) // Stop updating SeekBar
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            pauseMusic() // Pause music when the activity is paused
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(runnable) // Clean up the handler
    }
}
