package com.taibahai.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.network.interfaces.OnItemClick
import com.taibahai.BuildConfig
import com.taibahai.R
import com.taibahai.databinding.DialogInformationBinding


fun Activity.showToast(text: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, time).show()
}

fun Fragment.showToast(text: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.context, text, time).show()
}
fun Activity.hideKeyboard() {
    val view = this.findViewById<View>(android.R.id.content)
    if (view != null) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
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


fun Context.shareApp() {

    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        this.getString(R.string.share_text) + " ⬇️\n https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    )
    this.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun Context.openPlayStoreForRating() {
    val packageName = packageName
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}


fun EditText.addTextWatcher(onTextChanged: (Double) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString().trim()
            if (text.isNotEmpty()) {
                onTextChanged(text.toDouble())
            } else {
                onTextChanged(0.0)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })


}