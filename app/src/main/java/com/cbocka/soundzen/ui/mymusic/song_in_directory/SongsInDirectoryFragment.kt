package com.cbocka.soundzen.ui.mymusic.song_in_directory

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.MusicDirectory
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.databinding.FragmentSongsInDirectoryBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.base.FragmentProgressDialog
import com.cbocka.soundzen.ui.base.OneOptionDialog
import com.cbocka.soundzen.ui.base.TwoOptionsDialog
import com.cbocka.soundzen.ui.mymusic.song_in_directory.adapter.SongsInDirectoryAdapter
import com.cbocka.soundzen.ui.mymusic.song_in_directory.usecase.SongsInDirectoryState
import com.cbocka.soundzen.ui.mymusic.song_in_directory.usecase.SongsInDirectoryViewModel
import com.cbocka.soundzen.utils.Locator

class SongsInDirectoryFragment : Fragment() {
    private var _binding : FragmentSongsInDirectoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel : SongsInDirectoryViewModel by viewModels()

    private lateinit var songsAdapter : SongsInDirectoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.directoryPath = requireArguments().getString(MusicDirectory.KEY, "/storage/emulated/0/Music/")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSongsInDirectoryBinding.inflate(inflater, container, false)

        (activity as MainActivity).setBottomNavGone()

        setBackgroundColor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.clearFocus()

        initRecyclerView()

        binding.btnGoToDirectory.setOnClickListener {
            openFileDirectory()
        }

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it) {
                is SongsInDirectoryState.Loading -> onLoading(it.show)
                SongsInDirectoryState.NoData -> onNoData()
                SongsInDirectoryState.Success -> onSuccess()
                else -> {}
            }
        })

        viewModel.getSongList()

        binding.searchView.setOnQueryTextListener(SearchViewText())
    }

    override fun onPause() {
        super.onPause()
        binding.searchView.clearFocus()
        binding.searchView.setQuery("", false)
        viewModel.resetState()
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        (activity as MainActivity).setBottomNavGone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {

        songsAdapter = SongsInDirectoryAdapter(requireContext(), { song, list -> onPlaySong(song, list) }, {showOptionsDialog(it)})

        binding.rvMyMusic.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyMusic.adapter = songsAdapter
    }

    private fun showOptionsDialog(song: Song): Boolean {
        val options = arrayOf(getString(R.string.alert_dialog_option1), getString(R.string.alert_dialog_option2), getString(R.string.alert_dialog_option3))

        AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (viewModel.isFavourite(song))
                            removeFromFavourites(song)
                        else
                            addToFavourites(song)
                    }
                    1 -> {}
                    2 -> deleteSong(song)
                }
            }
            .setNegativeButton(getString(R.string.alert_dialog_cancel), null)
            .show()

        return true
    }

    private fun onPlaySong(song: Song, list: List<Song>) {
        val playerOrder = Locator.settingsPreferencesRepository.getString(
            getString(R.string.preference_player_order_key), "SEC")

        val tmpList = mutableListOf<Song>()

        when (playerOrder) {
            "SEC" ->  {
                tmpList.add(song)

                tmpList.addAll(list.subList(list.indexOf(song) + 1, list.size))
                tmpList.addAll(list.subList(0, list.indexOf(song)))
            }
            "RND" -> {
                tmpList.add(song)
                tmpList.addAll(list.filter { song != it }.shuffled())
            }
        }

        findNavController().navigate(R.id.action_songsInDirectoryFragment_to_songDetailsFragment)

        (activity as MainActivity).startPlayer(tmpList)
        (activity as MainActivity).updatePlayerSongList(tmpList)
    }

    private fun deleteSong(song: Song) : Boolean {

        val dialog = TwoOptionsDialog.newInstance(
            getString(R.string.delete_song_dialog_title),
            getString(R.string.delete_song_dialog_message))

        dialog.show((context as AppCompatActivity).supportFragmentManager, TwoOptionsDialog.TAG)

        dialog.parentFragmentManager.setFragmentResultListener(TwoOptionsDialog.request, viewLifecycleOwner) {
                _, bundle ->
            val result = bundle.getBoolean(TwoOptionsDialog.result)

            if (result) {

                val resource = viewModel.deleteSong(song)

                if (resource) {
                    songsAdapter.notifyDataSetChanged()
                    viewModel.getSongList()
                    Toast.makeText(requireContext(), getString(R.string.Toast_song_deleted_message), Toast.LENGTH_SHORT).show()

                } else {
                    val dialogError = OneOptionDialog.newInstance(
                        getString(R.string.delete_song_error_dialog_title),
                        getString(R.string.delete_song_error_dialog_message))

                    dialogError.show((context as AppCompatActivity).supportFragmentManager, OneOptionDialog.KEY)
                }
            }
        }

        return true
    }

    private fun addToFavourites(song: Song) {
        viewModel.addSongToFavourites(song)
        songsAdapter.notifyDataSetChanged()
    }

    private fun removeFromFavourites(song: Song) {
        viewModel.removeSongFromFavourites(song)
        songsAdapter.notifyDataSetChanged()
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.clSongsInDirectory.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.clSongsInDirectory.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun onNoData() {
        binding.tvMyMusicNoData2.text = getString(R.string.my_music_list_no_data2,
            Locator.settingsPreferencesRepository.getString(
                getString(R.string.preference_location_path_key),"/storage/emulated/0/Music/"))

        binding.rvMyMusic.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        binding.imgCircle.visibility = View.VISIBLE
        binding.animationViewItemList.visibility = View.VISIBLE
        binding.tvMyMusicNoData.visibility = View.VISIBLE
        binding.tvMyMusicNoData2.visibility = View.VISIBLE
        binding.btnGoToDirectory.visibility = View.VISIBLE
    }

    private fun onLoading(showLoading : Boolean) {
        if (showLoading) {
            FragmentProgressDialog.title = getString(R.string.mymusic_loading_title)
            findNavController().navigate(R.id.action_songsInDirectoryFragment_to_fragmentProgressDialog)
        }
        else
            findNavController().popBackStack()
    }

    private fun onSuccess() {
        binding.rvMyMusic.visibility = View.VISIBLE
        binding.searchView.visibility = View.VISIBLE
        binding.imgCircle.visibility = View.GONE
        binding.animationViewItemList.visibility = View.GONE
        binding.tvMyMusicNoData.visibility = View.GONE
        binding.tvMyMusicNoData2.visibility = View.GONE
        binding.btnGoToDirectory.visibility = View.GONE

        Locator.loadSongsFromDirectory = false

        songsAdapter.submitList(viewModel.allSongs)
    }

    private fun openFileDirectory() {

        val path = Locator.settingsPreferencesRepository.getString(getString(R.string.preference_location_path_key),"/storage/emulated/0/Music/")
        val uri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_PICK)

        intent.setDataAndType(uri, "*/*")
        startActivity(intent)
    }

    inner class SearchViewText() : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {return true}

        override fun onQueryTextChange(newText: String?): Boolean {

            viewModel.filteredSongs = if (newText.isNullOrEmpty()) {
                viewModel.allSongs
            } else {
                viewModel.allSongs.filter { song ->
                    song.artist.lowercase().contains(newText.lowercase()) ||
                            song.songName.lowercase().contains(newText.lowercase())
                }
            }

            songsAdapter.submitList(viewModel.filteredSongs)
            return true
        }
    }
}