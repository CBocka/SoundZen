package com.cbocka.soundzen.ui.mymusic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentMyMusicParentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyMusicParentFragment : Fragment() {

    private var _binding : FragmentMyMusicParentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyMusicParentBinding.inflate(inflater, container, false)
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
        val tabAdapter = MyMusicPagerAdapter(childFragmentManager, lifecycle, binding.tabLayout.tabCount)

        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            when (position) {
                0 -> {
                    tab.text = getString(R.string.all_music_tab_title)
                }
                1 -> {
                    tab.text = getString(R.string.directories_tab_title)
                }
                2 -> {
                    tab.text = getString(R.string.fav_music_tab_title)
                }
                3 -> {
                    tab.text = getString(R.string.playlist_tab_title)
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