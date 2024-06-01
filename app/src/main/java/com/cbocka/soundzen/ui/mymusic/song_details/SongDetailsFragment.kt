package com.cbocka.soundzen.ui.mymusic.song_details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.databinding.FragmentSongDetailsBinding
import com.cbocka.soundzen.music_player.service.MusicService
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.utils.Locator

class SongDetailsFragment : Fragment() {

    private var _binding: FragmentSongDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var musicService: MusicService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSongDetailsBinding.inflate(inflater, container, false)

        (activity as MainActivity).setBottomNavGone()
        setBackgroundColor()
        (activity as MainActivity).hidePlaybackControlsCardView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        musicService = (activity as MainActivity).musicService!!

        setUpDetails(MusicService.musicFiles[MusicService.currentSongIndex])
        setupPlaybackControls()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as MainActivity).setBottomNavVisible()
        (activity as MainActivity).showPlaybackControlsCardView()

        _binding = null
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.clSongDetails.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.clSongDetails.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun setUpDetails(song: Song) {
        binding.tvSongArtist.text = song.artist
        binding.tvSongTitle.text = song.songName
        binding.swFavourite.isChecked = song.isFavorite
    }

    private fun setupPlaybackControls() {
        binding.btnPlayPause.setOnClickListener {
            if (MusicService.isPlaying) {
                musicService.pause()
            } else {
                musicService.resume()
            }
        }

        binding.btnPrevious.setOnClickListener {
            resetSeekBar()

            musicService.playPrevious()

            setUpDetails(MusicService.musicFiles[MusicService.currentSongIndex])
        }

        binding.btnNext.setOnClickListener {
            resetSeekBar()

            musicService.playNext()

            setUpDetails(MusicService.musicFiles[MusicService.currentSongIndex])
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = MusicService.exoPlayer!!.duration
                    val newPosition = (duration * progress) / seekBar!!.max
                    musicService.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun resetSeekBar() {
        binding.seekBar.progress = 0
        binding.seekBar.max = MusicService.exoPlayer!!.duration.toInt()
    }
}