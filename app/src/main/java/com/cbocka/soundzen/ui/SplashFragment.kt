package com.cbocka.soundzen.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding : FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).setAppBarGone()
        (activity as MainActivity).setBottomNavGone()

        val r = Runnable {
            findNavController().navigate(R.id.action_splashFragment_to_myMusicFragment)
        }
        Handler(Looper.getMainLooper()).postDelayed(r, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}