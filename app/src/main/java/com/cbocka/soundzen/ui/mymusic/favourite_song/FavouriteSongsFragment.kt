package com.cbocka.soundzen.ui.mymusic.favourite_song

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
import com.cbocka.soundzen.data.model.Song
import com.cbocka.soundzen.databinding.FragmentFavouriteSongsBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.base.FragmentProgressDialog
import com.cbocka.soundzen.ui.base.OneOptionDialog
import com.cbocka.soundzen.ui.base.TwoOptionsDialog
import com.cbocka.soundzen.ui.mymusic.all_music.usecase.MyMusicListState
import com.cbocka.soundzen.ui.mymusic.favourite_song.adapter.FavouriteMusicAdapter
import com.cbocka.soundzen.ui.mymusic.favourite_song.usecase.FavouriteMusicState
import com.cbocka.soundzen.ui.mymusic.favourite_song.usecase.FavouriteMusicViewModel
import com.cbocka.soundzen.utils.Locator

class FavouriteSongsFragment : Fragment() {

    private var _binding : FragmentFavouriteSongsBinding? = null
    private val binding get() = _binding!!

    private val viewModel : FavouriteMusicViewModel by viewModels()

    private lateinit var songsAdapter : FavouriteMusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteSongsBinding.inflate(inflater, container, false)

        (activity as MainActivity).setBottomNavVisible()

        setBackgroundColor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it) {
                FavouriteMusicState.NoData -> onNoData()
                FavouriteMusicState.Success -> onSuccess()
                else -> {}
            }
        })

        viewModel.getSongList()
    }

    override fun onPause() {
        super.onPause()
        viewModel.resetState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSongList()
        songsAdapter.submitList(viewModel.favoriteSongs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {

        songsAdapter = FavouriteMusicAdapter(requireContext(), { song, list -> onPlaySong(song, list) }, {deleteSong(it)})

        binding.rvFavourites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavourites.adapter = songsAdapter
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

        findNavController().navigate(R.id.action_myMusicParentFragment_to_songDetailsFragment)

        (activity as MainActivity).startPlayer(tmpList)
        (activity as MainActivity).updatePlayerSongList(tmpList)
    }

    private fun deleteSong(song: Song) : Boolean {

        val dialog = TwoOptionsDialog.newInstance(
            getString(R.string.delete_favourite_dialog_title),
            getString(R.string.delete_favourite_dialog_message))

        dialog.show((context as AppCompatActivity).supportFragmentManager, TwoOptionsDialog.TAG)

        dialog.parentFragmentManager.setFragmentResultListener(TwoOptionsDialog.request, viewLifecycleOwner) {
                _, bundle ->
            val result = bundle.getBoolean(TwoOptionsDialog.result)

            if (result) {

                val resource = viewModel.deleteSong(song)

                if (resource) {
                    songsAdapter.notifyDataSetChanged()

                    Toast.makeText(requireContext(), getString(R.string.Toast_favourite_deleted_message), Toast.LENGTH_SHORT).show()

                } else {
                    val dialogError = OneOptionDialog.newInstance(
                        getString(R.string.delete_favourite_error_dialog_title),
                        getString(R.string.delete_favourite_error_dialog_message))

                    dialogError.show((context as AppCompatActivity).supportFragmentManager, OneOptionDialog.KEY)
                }
            }
        }

        return true
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.clFavouritesList.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.clFavouritesList.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun onNoData() {
        binding.rvFavourites.visibility = View.GONE
        binding.imgCircle.visibility = View.VISIBLE
        binding.animationViewItemList.visibility = View.VISIBLE
        binding.tvFavouritesNoData.visibility = View.VISIBLE
        binding.tvFavouritesNoData2.visibility = View.VISIBLE
    }

    private fun onSuccess() {
        binding.rvFavourites.visibility = View.VISIBLE
        binding.imgCircle.visibility = View.GONE
        binding.animationViewItemList.visibility = View.GONE
        binding.tvFavouritesNoData.visibility = View.GONE
        binding.tvFavouritesNoData2.visibility = View.GONE

        songsAdapter.submitList(viewModel.favoriteSongs)
    }
}