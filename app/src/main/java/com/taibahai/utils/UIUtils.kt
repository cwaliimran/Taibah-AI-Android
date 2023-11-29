package com.taibahai.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Activity.showToast(text: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, time).show()
}

fun Fragment.showToast(text: String, time: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.context, text, time).show()
}