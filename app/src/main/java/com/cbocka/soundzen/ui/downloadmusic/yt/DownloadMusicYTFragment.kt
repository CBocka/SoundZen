package com.cbocka.soundzen.ui.downloadmusic.yt

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentDownloadMusicYtBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.base.FragmentProgressDialog
import com.cbocka.soundzen.ui.base.OneOptionDialog
import com.cbocka.soundzen.ui.downloadmusic.yt.usecase.DownloadMusicYTState
import com.cbocka.soundzen.ui.downloadmusic.yt.usecase.DownloadMusicYTViewModel
import com.cbocka.soundzen.utils.Locator
import com.google.android.material.textfield.TextInputLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch

class DownloadMusicYTFragment : Fragment() {

    private var _binding: FragmentDownloadMusicYtBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DownloadMusicYTViewModel by viewModels()

    private lateinit var ytPlayer: YouTubePlayer

    private lateinit var youTubePlayerView : YouTubePlayerView

    private var downloadTW : DownloadTextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadMusicYtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initYoutubePlayer()
        initViewModel()

        binding.tieYouTubeLink.addTextChangedListener(ArtistAndSongTextWatcher(binding.tilYouTubeLink))
        binding.tieArtistName.addTextChangedListener(ArtistAndSongTextWatcher(binding.tilArtistName))
        binding.tieSongName.addTextChangedListener(ArtistAndSongTextWatcher(binding.tilSongName))

        binding.imgCopyFromClipboard.setOnClickListener {
            binding.tieYouTubeLink.setText(pasteFromClipboard()!!)
        }

        binding.btnDownloadMP3.setOnClickListener {
            viewModel.validateSong()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.resetState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initYoutubePlayer() {
        youTubePlayerView = YouTubePlayerView(requireContext())
        binding.clYoutubePlayer.addView(youTubePlayerView)

        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.enableAutomaticInitialization = false
        youTubePlayerView.initialize(YTPlayerListener())

        downloadTW = DownloadTextWatcher()
        binding.tieYouTubeLink.addTextChangedListener(downloadTW)
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it) {
                DownloadMusicYTState.UrlIsMandatory -> setUrlMandatoryError()
                DownloadMusicYTState.ArtistIsMandatory -> setArtistIsMandatoryError()
                DownloadMusicYTState.SongNameIsMandatory -> setSongNameIsMandatoryError()
                DownloadMusicYTState.UrlNotValid -> setUrlNotValidError()
                is DownloadMusicYTState.Loading -> onLoading(it.showLoading)
                DownloadMusicYTState.SongOK -> onSongOk()
                DownloadMusicYTState.Success -> onSuccess()
                else -> {}
            }
        })
    }

    private fun onLoading(showLoading: Boolean) {
        if (showLoading) {
            FragmentProgressDialog.title = getString(R.string.download_loading_title)
            findNavController().navigate(R.id.action_downloadMusicParentFragment_to_fragmentProgressDialog)
        }
        else
            findNavController().popBackStack()
    }

    private fun setUrlMandatoryError() {
        binding.tilYouTubeLink.error = getString(R.string.url_mandatory_error)
    }

    private fun setArtistIsMandatoryError() {
        binding.tilArtistName.error = getString(R.string.artist_mandatory_error)
    }

    private fun setSongNameIsMandatoryError() {
        binding.tilSongName.error = getString(R.string.song_mandatory_error)
    }

    private fun setUrlNotValidError() {
        binding.tilYouTubeLink.error = getString(R.string.url_invalid_error)
    }

    private fun onSongOk() {
        lifecycleScope.launch {
            (activity as MainActivity).openFolderPicker()

            viewModel.downloadSong((activity as MainActivity).downloadPath)
        }
    }

    private fun onSuccess() {
        binding.tieYouTubeLink.setText("")
        binding.tieArtistName.setText("")
        binding.tieSongName.setText("")
        binding.clDownloadFragment.clearFocus()

        Locator.loadSongs = true
        Locator.loadDirectories = true
        Locator.loadSongsFromDirectory = true

        val dialog = OneOptionDialog.newInstance(getString(R.string.dialog_title), getString(R.string.dialog_message))
        dialog.show((context as AppCompatActivity).supportFragmentManager, OneOptionDialog.KEY)
    }

    private fun pasteFromClipboard(): String? {
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

        return clipboardManager?.let {
            if (it.hasPrimaryClip() && it.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
                val item = it.primaryClip?.getItemAt(0)
                item?.text?.toString()
            } else {
                ""
            }
        }
    }

    inner class DownloadTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.getVideoId(s.toString())
            try {
                ytPlayer.loadVideo(viewModel.videoId, 0f)
            } catch (_: Exception) { }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    inner class ArtistAndSongTextWatcher(private val til : TextInputLayout) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            til.error = null
        }
    }

    inner class YTPlayerListener : YouTubePlayerListener {
        override fun onApiChange(youTubePlayer: YouTubePlayer) {}

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {}

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {}

        override fun onPlaybackQualityChange(
            youTubePlayer: YouTubePlayer,
            playbackQuality: PlayerConstants.PlaybackQuality
        ) {}

        override fun onPlaybackRateChange(
            youTubePlayer: YouTubePlayer,
            playbackRate: PlayerConstants.PlaybackRate
        ) {}

        override fun onReady(youTubePlayer: YouTubePlayer) {
            ytPlayer = youTubePlayer
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {}

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {}

        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}

        override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {}
    }
}