package com.cbocka.soundzen.ui.downloadmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentDownloadMusicParentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DownloadMusicParentFragment : Fragment() {

    private var _binding : FragmentDownloadMusicParentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadMusicParentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTabLayout() {
        val tabAdapter = DownloadMusicPagerAdapter(childFragmentManager, lifecycle, binding.tabLayout.tabCount)

        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            when (position) {
                0 -> {
                    tab.text = "YouTube Link"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_download_youtube)
                }
                1 -> {
                    tab.text = getString(R.string.download_search_tab_text)
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_download_search)
                }
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                binding.viewPager.currentItem = position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}