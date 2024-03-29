package com.cbocka.soundzen.ui.mymusic

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentMyMusicBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.base.FragmentProgressDialog
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

        binding.btnGoToDirectory.setOnClickListener {
            openFileDirectory()
        }

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it) {
                is MyMusicListState.Loading -> onLoading(it.show)
                MyMusicListState.NoData -> onNoData()
                MyMusicListState.Success -> onSuccess()
                else -> {}
            }
        })

        viewModel.getSongList()
    }

    override fun onPause() {
        super.onPause()
        viewModel.resetState()
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
        binding.imgCircle.visibility = View.VISIBLE
        binding.animationViewItemList.visibility = View.VISIBLE
        binding.tvMyMusicNoData.visibility = View.VISIBLE
        binding.tvMyMusicNoData2.visibility = View.VISIBLE
        binding.btnGoToDirectory.visibility = View.VISIBLE
    }

    private fun onLoading(showLoading : Boolean) {
        if (showLoading) {
            FragmentProgressDialog.title = getString(R.string.mymusic_loading_title)
            findNavController().navigate(R.id.action_myMusicFragment_to_fragmentProgressDialog)
        }
        else
            findNavController().popBackStack()
    }

    private fun onSuccess() {
        binding.rvMyMusic.visibility = View.VISIBLE
        binding.imgCircle.visibility = View.GONE
        binding.animationViewItemList.visibility = View.GONE
        binding.tvMyMusicNoData.visibility = View.GONE
        binding.tvMyMusicNoData2.visibility = View.GONE
        binding.btnGoToDirectory.visibility = View.GONE

        Locator.loadSongs = false

        songsAdapter.submitList(viewModel.allSongs)
    }

    private fun openFileDirectory() {
        val path = "/storage/emulated/0/Music"
        val uri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_PICK)

        intent.setDataAndType(uri, "*/*")
        startActivity(intent)
    }
}