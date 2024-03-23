package com.cbocka.soundzen.ui.mymusic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cbocka.soundzen.R
import com.cbocka.soundzen.ui.MainActivity

class MyMusicFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        (activity as MainActivity).setBottomNavVisible()

        return inflater.inflate(R.layout.fragment_my_music, container, false)
    }
}