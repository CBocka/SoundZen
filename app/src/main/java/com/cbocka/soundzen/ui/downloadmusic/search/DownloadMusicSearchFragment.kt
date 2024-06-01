package com.cbocka.soundzen.ui.downloadmusic.search

import android.icu.text.CaseMap.Title
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentDownloadMusicSearchBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.base.FragmentProgressDialog
import com.cbocka.soundzen.ui.base.OneOptionDialog
import com.cbocka.soundzen.ui.base.TwoOptionsDialog
import com.cbocka.soundzen.ui.downloadmusic.search.usecase.DownloadMusicSearchState
import com.cbocka.soundzen.ui.downloadmusic.search.usecase.DownloadMusicSearchViewModel
import com.cbocka.soundzen.utils.Locator
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class DownloadMusicSearchFragment : Fragment() {

    private var _binding: FragmentDownloadMusicSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DownloadMusicSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadMusicSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        binding.tieArtistName.addTextChangedListener(ArtistAndSongTextWatcher(binding.tilArtistName))
        binding.tieSongName.addTextChangedListener(ArtistAndSongTextWatcher(binding.tilSongName))

        binding.btnDownloadSearchMP3.setOnClickListener {
            viewModel.validateSong(requireContext())
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

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                DownloadMusicSearchState.ArtistIsMandatory -> setArtistIsMandatoryError()
                DownloadMusicSearchState.SongNameIsMandatory -> setSongNameIsMandatoryError()
                is DownloadMusicSearchState.Loading -> onLoading(it.showLoading, it.title)
                DownloadMusicSearchState.SongNotOK -> onSongNotOK()
                DownloadMusicSearchState.SongOK -> onSongOK()
                DownloadMusicSearchState.Success -> onSuccess()
                else -> {}
            }
        })
    }

    private fun onSongNotOK() {
        val dialog = OneOptionDialog.newInstance(getString(R.string.search_error_dialog_title), getString(R.string.search_error_dialog_message))
        dialog.show((context as AppCompatActivity).supportFragmentManager, OneOptionDialog.KEY)
    }

    private fun onSongOK() {
        var mediaPlayer: MediaPlayer?

        mediaPlayer = MediaPlayer().apply {
            setDataSource(viewModel.songMP3Search!!.url)
            prepareAsync()
            setOnPreparedListener { start() }
        }

        val dialog = TwoOptionsDialog.newInstance(
            getString(R.string.search_dialog_title),
            getString(R.string.search_dialog_message)
        )
        dialog.show((context as AppCompatActivity).supportFragmentManager, TwoOptionsDialog.TAG)

        dialog.parentFragmentManager.setFragmentResultListener(
            TwoOptionsDialog.request,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getBoolean(TwoOptionsDialog.result)

            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            if (result) {
                lifecycleScope.launch {
                    (activity as MainActivity).openFolderPicker()

                    viewModel.downloadSong((activity as MainActivity).downloadPath, requireContext())
                }
            }
        }
    }

    private fun setArtistIsMandatoryError() {
        binding.tilArtistName.error = getString(R.string.artist_mandatory_error)
    }

    private fun setSongNameIsMandatoryError() {
        binding.tilSongName.error = getString(R.string.song_mandatory_error)
    }

    private fun onLoading(showLoading: Boolean, title: String) {
        if (showLoading) {
            FragmentProgressDialog.title = title
            findNavController().navigate(R.id.action_downloadMusicParentFragment_to_fragmentProgressDialog)
        } else
            findNavController().popBackStack()
    }

    private fun onSuccess() {
        binding.tieArtistName.setText("")
        binding.tieSongName.setText("")
        binding.clSearchDownload.clearFocus()

        Locator.loadSongs = true
        Locator.loadDirectorySongs = true

        val dialog = OneOptionDialog.newInstance(getString(R.string.dialog_title), getString(R.string.dialog_message))
        dialog.show((context as AppCompatActivity).supportFragmentManager, OneOptionDialog.KEY)
    }

    inner class ArtistAndSongTextWatcher(private val til: TextInputLayout) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            til.error = null
        }
    }
}