package com.cbocka.soundzen.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cbocka.soundzen.MainActivity
import com.cbocka.soundzen.R

class MainFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setAppBarVisible()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}