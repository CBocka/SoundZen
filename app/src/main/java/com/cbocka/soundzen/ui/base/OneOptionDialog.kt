package com.cbocka.soundzen.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class OneOptionDialog() : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = requireArguments().getString(title)
        val message = requireArguments().getString(message)

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(title)
        builder.setMessage(message)

        builder.setCancelable(false)

        builder.setPositiveButton(android.R.string.ok, null)

        return builder.create()
    }

    companion object {
        const val title = "title"
        const val message = "message"
        const val KEY = "ONE_OPTION_DIALOG"

        fun newInstance(title: String, message: String): OneOptionDialog {
            val fragment = OneOptionDialog()
            val args = Bundle()
            args.putString(Companion.title, title)
            args.putString(Companion.message, message)
            fragment.arguments = args
            return fragment
        }
    }
}