package com.example.shopmate.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.shopmate.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object UiUtils {
    var dialog: AlertDialog? = null

    fun showDialog(context: Context){
        dialog = MaterialAlertDialogBuilder(context)
            .setView(R.layout.loading_layout)
            .setCancelable(false)
            .create()

        dialog!!.show()
    }

    fun dismissDialog(){
        dialog!!.dismiss()
    }
}