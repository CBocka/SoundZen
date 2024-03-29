package com.cbocka.soundzen.ui.downloadmusic

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cbocka.soundzen.ui.downloadmusic.search.DownloadMusicSearchFragment
import com.cbocka.soundzen.ui.downloadmusic.yt.DownloadMusicYTFragment

class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private var mNumOfTabs: Int) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return mNumOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DownloadMusicYTFragment()
            1 -> DownloadMusicSearchFragment()
            else -> Fragment()
        }
    }
}
