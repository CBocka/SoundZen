package com.cbocka.soundzen.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class TwoOptionsDialog() : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = requireArguments().getString(title)
        val message = requireArguments().getString(message)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)

        builder.setPositiveButton(android.R.string.ok)
        { _, _ ->
            val bundle = Bundle()
            bundle.putBoolean(result, true)
            requireActivity().supportFragmentManager.setFragmentResult(request, bundle)
        }

        builder.setNegativeButton(
            android.R.string.cancel
        )
        { _, _ -> dismiss() }

        return builder.create()
    }

    companion object {
        const val title = "title"
        const val message = "message"
        const val request = "request"
        const val result = "result"
        const val TAG = "TWO_OPTIONS_DIALOG"

        fun newInstance(title: String, message: String): TwoOptionsDialog {
            val fragment = TwoOptionsDialog()
            val args = Bundle()
            args.putString(Companion.title, title)
            args.putString(Companion.message, message)
            fragment.arguments = args
            return fragment
        }
    }
}