package com.cbocka.soundzen.ui.mymusic.song_details

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

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

        binding.swLoop.isChecked = MusicService.isLooping

        binding.swLoop.setOnCheckedChangeListener { _, isChecked ->
            musicService.setLooping(isChecked)
        }

        MusicService.exoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY || playbackState == ExoPlayer.STATE_BUFFERING) {
                    val duration = MusicService.exoPlayer?.duration ?: 0
                    val currentPosition = MusicService.exoPlayer?.currentPosition ?: 0
                    binding.seekBar.max = duration.toInt()
                    binding.seekBar.progress = currentPosition.toInt()
                    binding.tvCurrentTime.text = getTimeString(currentPosition)

                    if (playbackState == ExoPlayer.STATE_READY) {
                        binding.tvDuration.text = getTimeString(duration)
                        handler.post(updateSeekBar)
                        startUpdateCurrentTime()
                    } else {
                        handler.removeCallbacks(updateSeekBar)
                        stopUpdateCurrentTime()
                    }
                } else if (playbackState == ExoPlayer.STATE_ENDED) {
                    setUpDetails(MusicService.musicFiles[MusicService.currentSongIndex])
                    stopUpdateCurrentTime()
                }
            }
        })

        binding.imgFav.setOnClickListener {
            val currentSong = MusicService.musicFiles[MusicService.currentSongIndex]
            currentSong.isFavorite = !currentSong.isFavorite
            // Aquí deberías guardar el estado de la canción favorita en tu base de datos o preferencias
            // Puedes usar el método setUpDetails para actualizar la vista con el nuevo estado
            setUpDetails(currentSong)
        }
    }

    override fun onResume() {
        super.onResume()
        setUpDetails(MusicService.musicFiles[MusicService.currentSongIndex])
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as MainActivity).setBottomNavVisible()
        (activity as MainActivity).showPlaybackControlsCardView()

        handler.removeCallbacks(updateSeekBar)
    }

    private fun setBackgroundColor() {

        val darkTheme = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme) {
            binding.clSongDetails.setBackgroundColor(Color.parseColor("#141414"))
            binding.btnNext.setColorFilter(Color.WHITE)
            binding.btnPlayPause.setColorFilter(Color.WHITE)
            binding.btnPrevious.setColorFilter(Color.WHITE)
        }
        else {
            binding.clSongDetails.setBackgroundColor(Color.parseColor("#ffffff"))
            binding.btnNext.setColorFilter(Color.BLACK)
            binding.btnPlayPause.setColorFilter(Color.BLACK)
            binding.btnPrevious.setColorFilter(Color.BLACK)
        }
    }

    private fun setUpDetails(song: Song) {
        binding.tvSongArtist.text = song.artist
        binding.tvSongTitle.text = song.songName

        updateFavoriteImage()
    }

    private fun setupPlaybackControls() {
        binding.btnPlayPause.setOnClickListener {

            if (MusicService.isPlaying) {
                musicService.pause()
                binding.btnPlayPause.setImageResource(R.drawable.play_circle)

            } else {
                musicService.resume()
                binding.btnPlayPause.setImageResource(R.drawable.pause_circle)
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

    private val handler = Handler()

    private val updateSeekBar = object : Runnable {
        override fun run() {
            val currentPosition = MusicService.exoPlayer?.currentPosition ?: 0
            binding.seekBar.progress = currentPosition.toInt()
            handler.postDelayed(this, 1000)
        }
    }

    private fun updateFavoriteImage() {
        if (MusicService.musicFiles[MusicService.currentSongIndex].isFavorite) {
            binding.imgFav.setImageResource(R.drawable.heart)
        } else {
            binding.imgFav.setImageResource(R.drawable.heart_outline)
        }
    }

    private fun getTimeString(timeMs: Long): String {
        val minutes = (timeMs / 1000) / 60
        val seconds = (timeMs / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private val updateCurrentTimeRunnable = object : Runnable {
        override fun run() {
            val currentPosition = MusicService.exoPlayer?.currentPosition ?: 0
            binding.seekBar.progress = currentPosition.toInt()
            binding.tvCurrentTime.text = getTimeString(currentPosition)
            handler.postDelayed(this, 1000)
        }
    }

    private fun startUpdateCurrentTime() {
        handler.post(updateCurrentTimeRunnable)
    }

    private fun stopUpdateCurrentTime() {
        handler.removeCallbacks(updateCurrentTimeRunnable)
    }

}