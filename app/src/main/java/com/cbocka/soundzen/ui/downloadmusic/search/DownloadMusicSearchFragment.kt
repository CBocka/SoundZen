package com.cbocka.soundzen.ui.downloadmusic.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cbocka.soundzen.databinding.FragmentDownloadMusicSearchBinding

class DownloadMusicSearchFragment : Fragment() {

    private var _binding: FragmentDownloadMusicSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadMusicSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
}