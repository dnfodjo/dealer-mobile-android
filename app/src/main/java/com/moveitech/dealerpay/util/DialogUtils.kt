package com.moveitech.dealerpay.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.moveitech.dealerpay.R

object  DialogUtils {
        fun getProgressDialog(context: Context): AlertDialog {
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = inflater.inflate(R.layout.progress_dialog_layout, null)
            dialogBuilder.setView(dialogView)

            val alertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCancelable(false)
            return alertDialog
        }


}