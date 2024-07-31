package com.taibahai.utils

import android.app.Activity
import com.github.dhaval2404.imagepicker.ImagePicker

fun Activity.getPicker(): ImagePicker.Builder {
    return ImagePicker.with(this)
        .cropSquare()
        .compress(600)         //Final image size will be less than 1 MB(Optional) //default 1024
        .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
}