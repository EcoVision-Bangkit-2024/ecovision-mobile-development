package com.bangkit.ecovision.ui.add

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.bangkit.ecovision.R

class SuccessDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = android.app.AlertDialog.Builder(requireContext())

        // Inflate custom layout
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_success, null)

        // Bind custom views
        val btnOk: Button = view.findViewById(R.id.btn_ok)

        // Set button click listener
        btnOk.setOnClickListener {
            dismiss()  // Dismiss the dialog on "OK" click
        }

        builder.setView(view)
        return builder.create()
    }
}