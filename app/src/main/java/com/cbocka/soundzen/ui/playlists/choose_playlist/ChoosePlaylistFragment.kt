package com.cbocka.soundzen.ui.playlists.choose_playlist

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.data.repository.PlaylistRepository
import com.cbocka.soundzen.databinding.FragmentChoosePlaylistBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.playlists.choose_playlist.usecase.ChoosePlaylistViewModel
import com.cbocka.soundzen.utils.Locator

class ChoosePlaylistFragment : Fragment() {

    private var _binding: FragmentChoosePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChoosePlaylistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosePlaylistBinding.inflate(inflater, container, false)

        (activity as MainActivity).setBottomNavGone()
        setBackgroundColor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpinner()

        val song = requireArguments().getParcelable<Song>(Song.SONG_KEY)!!

        binding.addBtn.setOnClickListener {

            PlaylistRepository.instance.addSongToPlaylist(binding.spinnerPlaylists.selectedItem.toString(), song)
            Toast.makeText(requireContext(),
                getString(R.string.choose_playlist_success_toast),
                Toast.LENGTH_SHORT).show()

            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.clChoosePlaylist.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.clChoosePlaylist.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun initSpinner() {
        val playlists = viewModel.getPlaylists().keys

        val arrayPlaylists: Array<String> = playlists.toTypedArray()

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayPlaylists
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerPlaylists.adapter = spinnerAdapter
    }

}