package com.bangkit.ecovision.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bangkit.ecovision.R

class LoadingDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progressDialog = android.app.AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_loading) // You can create a custom layout for loading
            .setCancelable(false)
            .create()

        return progressDialog
    }
}
