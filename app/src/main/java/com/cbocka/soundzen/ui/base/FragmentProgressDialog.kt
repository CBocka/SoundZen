package com.cbocka.soundzen.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.cbocka.soundzen.R

class FragmentProgressDialog : DialogFragment() {
    companion object {
        var title: String = ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.fragment_dialog_progress, null)

        val builder = AlertDialog.Builder(requireContext())

        builder.setView(view)
        builder.setCancelable(false)
        builder.setTitle(title)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}
