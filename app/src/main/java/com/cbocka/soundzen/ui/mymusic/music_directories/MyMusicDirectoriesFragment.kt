package com.cbocka.soundzen.ui.mymusic.music_directories

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbocka.soundzen.R
import com.cbocka.soundzen.data.model.MusicDirectory
import com.cbocka.soundzen.databinding.FragmentMyMusicDirectoriesBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.base.FragmentProgressDialog
import com.cbocka.soundzen.ui.mymusic.music_directories.adapter.MusicDirectoriesAdapter
import com.cbocka.soundzen.ui.mymusic.music_directories.usecase.MusicDirectoriesState
import com.cbocka.soundzen.ui.mymusic.music_directories.usecase.MusicDirectoriesViewModel
import com.cbocka.soundzen.utils.Locator

class MyMusicDirectoriesFragment : Fragment() {

    private var _binding: FragmentMyMusicDirectoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MusicDirectoriesViewModel by viewModels()

    private lateinit var directoriesAdapter : MusicDirectoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyMusicDirectoriesBinding.inflate(inflater, container, false)

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
                is MusicDirectoriesState.Loading -> onLoading(it.show)
                MusicDirectoriesState.NoData -> onNoData()
                MusicDirectoriesState.Success -> onSuccess()
                else -> {}
            }
        })

        viewModel.getDirectoriesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.clMyMusicDirectories.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.clMyMusicDirectories.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun initRecyclerView() {

        directoriesAdapter = MusicDirectoriesAdapter(requireContext()) { showSongsFromDirectory(it) }

        binding.rvMyDirectories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyDirectories.adapter = directoriesAdapter
    }

    private fun showSongsFromDirectory(directory : MusicDirectory) {
        val bundle = Bundle()
        bundle.putString(MusicDirectory.KEY, directory.path)
        findNavController().navigate(R.id.action_myMusicParentFragment_to_songsInDirectoryFragment, bundle)
    }

    private fun onNoData() {
        binding.tvMyDirectoriesNoData2.text = getString(R.string.music_directories_list_no_data2,
            Locator.settingsPreferencesRepository.getString(
                getString(R.string.preference_location_path_key),"/storage/emulated/0/Music/"))

        binding.rvMyDirectories.visibility = View.GONE
        binding.imgCircle.visibility = View.VISIBLE
        binding.animationViewItemList.visibility = View.VISIBLE
        binding.tvMyDirectoriesNoData.visibility = View.VISIBLE
        binding.tvMyDirectoriesNoData2.visibility = View.VISIBLE
        binding.btnGoToDirectory.visibility = View.VISIBLE
    }

    private fun onLoading(showLoading : Boolean) {
        if (showLoading) {
            FragmentProgressDialog.title = getString(R.string.mymusic_loading_title)
            findNavController().navigate(R.id.action_myMusicParentFragment_to_fragmentProgressDialog)
        }
        else
            findNavController().popBackStack()
    }

    private fun onSuccess() {
        binding.rvMyDirectories.visibility = View.VISIBLE
        binding.imgCircle.visibility = View.GONE
        binding.animationViewItemList.visibility = View.GONE
        binding.tvMyDirectoriesNoData.visibility = View.GONE
        binding.tvMyDirectoriesNoData2.visibility = View.GONE
        binding.btnGoToDirectory.visibility = View.GONE

        Locator.loadDirectories = false
        Locator.loadSongsFromDirectory = true

        directoriesAdapter.submitList(viewModel.allDirectories)
    }

    private fun openFileDirectory() {

        val path = Locator.settingsPreferencesRepository.getString(getString(R.string.preference_location_path_key),"/storage/emulated/0/Music/")
        val uri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_PICK)

        intent.setDataAndType(uri, "*/*")
        startActivity(intent)
    }
}