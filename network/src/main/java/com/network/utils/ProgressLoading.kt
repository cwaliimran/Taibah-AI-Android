package com.network.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.network.R
import com.network.databinding.LayoutLoadingScreenBinding

object ProgressLoading {
    private var dialog: Dialog? = null
    fun Context.displayLoading(
        isShow: Boolean? = true,
        cancelable: Boolean? = false //default value
    ) {
        if (isShow!!) {

            val layoutInflater = LayoutInflater.from(this)
            val binding = LayoutLoadingScreenBinding.inflate(layoutInflater)
            dialog = Dialog(this)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(binding.root)
            dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
            dialog?.window?.setDimAmount(0.2f)
            dialog?.setCancelable(cancelable!!)
            try {
                dialog?.show()
            } catch (_: Exception) {
            }
        } else {
            try {
                if (dialog != null) {
                    dialog?.dismiss()
                }
            } catch (_: Exception) {
            }
        }
    }

}