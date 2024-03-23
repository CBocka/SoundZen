package com.cbocka.soundzen.ui.mymusic

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentMyMusicBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.mymusic.adapter.MyMusicListAdapter
import com.cbocka.soundzen.ui.mymusic.usecase.MyMusicListState
import com.cbocka.soundzen.ui.mymusic.usecase.MyMusicViewModel
import com.cbocka.soundzen.utils.Locator

class MyMusicFragment : Fragment() {

    private var _binding : FragmentMyMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MyMusicViewModel by viewModels()

    private lateinit var songsAdapter : MyMusicListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyMusicBinding.inflate(inflater, container, false)

        (activity as MainActivity).setBottomNavVisible()

        setBackgroundColor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it) {
                MyMusicListState.NoData -> onNoData()
                else -> onSuccess()
            }
        })

        viewModel.getSongList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        songsAdapter = MyMusicListAdapter(requireContext())
        binding.rvMyMusic.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyMusic.adapter = songsAdapter
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.rvMyMusic.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.rvMyMusic.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun onNoData() {
        binding.rvMyMusic.visibility = View.GONE
    }

    private fun onSuccess() {
        binding.rvMyMusic.visibility = View.VISIBLE
        songsAdapter.submitList(viewModel.allSongs)
    }
}