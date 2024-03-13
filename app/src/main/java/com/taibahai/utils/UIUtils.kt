package com.taibahai.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.network.interfaces.OnItemClick
import com.taibahai.databinding.DialogInformationBinding


fun Activity.showToast(text: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, time).show()
}

fun Fragment.showToast(text: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.context, text, time).show()
}


fun Context.genericDialog(listener: OnItemClick, cancelable: Boolean? = true) {
    val dialog = Dialog(this)
    val layoutInflater = LayoutInflater.from(this)
    val binding = DialogInformationBinding.inflate(layoutInflater)

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.setCancelable(cancelable ?: true)
    if (dialog.isShowing) return
    binding.btnYes.setOnClickListener {
        dialog.dismiss()
        listener.onClick(0, "login")

    }
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )
    dialog.show()
}