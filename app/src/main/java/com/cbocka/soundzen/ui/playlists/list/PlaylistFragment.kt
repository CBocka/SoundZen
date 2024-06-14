package com.cbocka.soundzen.ui.playlists.list

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.Playlist
import com.cbocka.soundzen.databinding.FragmentPlaylistBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.base.OneOptionDialog
import com.cbocka.soundzen.ui.base.TwoOptionsDialog
import com.cbocka.soundzen.ui.playlist.adapter.PlaylistAdapter
import com.cbocka.soundzen.ui.playlist.usecase.PlaylistState
import com.cbocka.soundzen.ui.playlist.usecase.PlaylistViewModel
import com.cbocka.soundzen.utils.Locator

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel : PlaylistViewModel by viewModels()

    private lateinit var playlistAdapter : PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        (activity as MainActivity).setBottomNavVisible()

        setBackgroundColor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it) {
                PlaylistState.NoData -> onNoData()
                PlaylistState.Success -> onSuccess()
                else -> {}
            }
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_playlistFragment_to_createPlaylistFragment)
        }

        viewModel.getPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.clPlaylist.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.clPlaylist.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun initRecyclerView() {

        playlistAdapter = PlaylistAdapter(requireContext(), { openPlaylist(it) }, {deletePlaylist(it)})

        binding.rvMyPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPlaylists.adapter = playlistAdapter
    }

    private fun openPlaylist(playlist: Playlist) {
        val bundle = Bundle()
        bundle.putString(Playlist.PLAYLIST_KEY, playlist.name)

        findNavController().navigate(R.id.action_playlistFragment_to_songPlaylistFragment, bundle)
    }

    private fun deletePlaylist(playlist: Playlist) : Boolean {

        val dialog = TwoOptionsDialog.newInstance(
            getString(R.string.delete_playlist_dialog_title),
            getString(R.string.delete_playlist_dialog_message))

        dialog.show((context as AppCompatActivity).supportFragmentManager, TwoOptionsDialog.TAG)

        dialog.parentFragmentManager.setFragmentResultListener(TwoOptionsDialog.request, viewLifecycleOwner) {
                _, bundle ->
            val result = bundle.getBoolean(TwoOptionsDialog.result)

            if (result) {

                val resource = viewModel.deletePlaylist(playlist)

                if (resource) {
                    viewModel.getPlaylists()
                    Toast.makeText(requireContext(), getString(R.string.Toast_playlist_deleted_message), Toast.LENGTH_SHORT).show()

                } else {
                    val dialogError = OneOptionDialog.newInstance(
                        getString(R.string.delete_playlist_error_dialog_title),
                        getString(R.string.delete_playlist_error_dialog_message))

                    dialogError.show((context as AppCompatActivity).supportFragmentManager, OneOptionDialog.KEY)
                }
            }
        }

        return true
    }

    private fun onNoData() {
        binding.rvMyPlaylists.visibility = View.GONE
        binding.imgCircle.visibility = View.VISIBLE
        binding.animationViewItemList.visibility = View.VISIBLE
        binding.tvPlaylistsNoData.visibility = View.VISIBLE
        binding.tvPlaylistsNoData2.visibility = View.VISIBLE
    }

    private fun onSuccess() {
        binding.rvMyPlaylists.visibility = View.VISIBLE
        binding.imgCircle.visibility = View.GONE
        binding.animationViewItemList.visibility = View.GONE
        binding.tvPlaylistsNoData.visibility = View.GONE
        binding.tvPlaylistsNoData2.visibility = View.GONE

        val playlistList = viewModel.allPlaylist.map { (name, songs) ->
            Playlist(name = name, songsIncluded = songs)
        }

        playlistAdapter.submitList(playlistList)
    }
}