package com.cbocka.soundzen.ui.playlists.create

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cbocka.soundzen.R
import com.cbocka.soundzen.databinding.FragmentCreatePlaylistBinding
import com.cbocka.soundzen.ui.MainActivity
import com.cbocka.soundzen.ui.playlists.create.usecase.CreatePlaylistState
import com.cbocka.soundzen.ui.playlists.create.usecase.CreatePlaylistViewModel
import com.cbocka.soundzen.utils.Locator
import com.google.android.material.textfield.TextInputLayout

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        (activity as MainActivity).setBottomNavGone()
        (activity as MainActivity).setCardViewPlayerGone()
        setBackgroundColor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        binding.fab.setOnClickListener {
            viewModel.validatePlaylist()
        }

        binding.tiePlaylistName.addTextChangedListener(CreatePlaylistTextWatcher(binding.tilPlaylistName))

        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when(it) {
                CreatePlaylistState.NameIsMandatoryError -> setNameIsMandatoryError()
                CreatePlaylistState.PlaylistAlreadyExistsError -> setPlaylistAlreadyExistsError()
                else -> onSuccess()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setBackgroundColor() {
        val darkTheme : Boolean = Locator.settingsPreferencesRepository.getBoolean(getString(R.string.preference_theme_key), false)

        if (darkTheme)
            binding.clCreatePlaylist.setBackgroundColor(Color.parseColor("#141414"))
        else
            binding.clCreatePlaylist.setBackgroundColor(Color.parseColor("#ffffff"))
    }

    private fun setNameIsMandatoryError() {
        binding.tilPlaylistName.error = getString(R.string.tie_create_playlist_error)
        binding.tiePlaylistName.requestFocus()
    }

    private fun setPlaylistAlreadyExistsError() {
        binding.tilPlaylistName.error = getString(R.string.tie_create_playlist_duplicate_error)
        binding.tiePlaylistName.requestFocus()
    }

    private fun onSuccess() {
        Toast.makeText(requireContext(), getString(R.string.create_playlist_success), Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    inner class CreatePlaylistTextWatcher(private val til: TextInputLayout): TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            til.error = null
        }
    }
}