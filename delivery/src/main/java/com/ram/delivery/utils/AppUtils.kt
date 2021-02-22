package com.ram.delivery.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.ram.delivery.R
import com.ram.delivery.other.DialogType
import kotlinx.android.synthetic.main.custom_popup_dialog.view.*

fun Context.showCustomDialog(
    id: Int,
    dialogType: DialogType,
    title: String,
    message: String,
    btnLeftText: String = "",
    btnRightText: String = "",
    cancelable:Boolean = true,
    confirmClickListener: (isConfirm: Boolean, id: Int) -> Unit
) {
    val dialogView = LayoutInflater.from(this)
        .inflate(R.layout.custom_popup_dialog, null)
    val builder = AlertDialog.Builder(this)
        .setView(dialogView)
    builder.setCancelable(cancelable)
    val alertDialog = builder.show()
    if (title.isEmpty())
        dialogView.title.visibility = View.GONE
    else
        dialogView.title.text = title
    dialogView.message.text = message
    if (dialogType == DialogType.ALERT) {
        dialogView.btnCancel.visibility = View.GONE
    } else {
        if (btnLeftText.isNotEmpty()) dialogView.btnCancel.text = btnLeftText
    }
    if (btnRightText.isNotEmpty()) dialogView.btnConfirm.text = btnRightText

    dialogView.btnConfirm.setOnClickListener {
        alertDialog.dismiss()
        confirmClickListener(true, id)
    }
    dialogView.btnCancel.setOnClickListener {
        alertDialog.dismiss()
        confirmClickListener(false, id)
    }
}

fun Context.showCustomDialog(
    dialogType: DialogType,
    title: String,
    message: String,
    btnLeftText: String = "",
    btnRightText: String = "",
    cancelable:Boolean = true,
    confirmClickListener: (isConfirm: Boolean, id: Int) -> Unit
) {
    showCustomDialog(-1, dialogType, title, message, btnLeftText, btnRightText, cancelable, confirmClickListener)
}