package com.cbocka.soundzen.ui.mymusic

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cbocka.soundzen.ui.mymusic.all_music.MyMusicFragment
import com.cbocka.soundzen.ui.mymusic.music_directories.MyMusicDirectoriesFragment

class MyMusicPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private var mNumOfTabs: Int) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return mNumOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyMusicFragment()
            1 -> MyMusicDirectoriesFragment()
            2 -> MyMusicFragment() //DownloadMusicYTFragment()
            3 -> MyMusicFragment() //DownloadMusicSearchFragment()
            else -> Fragment()
        }
    }
}