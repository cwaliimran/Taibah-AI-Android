package com.taibahai.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.taibahai.R


class BindingUtils {
    companion object {
        @JvmStatic
        @BindingAdapter("android:loadImage")
        fun loadImage(view: ImageView, imageUrl: String?) {
            Glide.with(view.context).load(imageUrl).placeholder(com.network.R.mipmap.ic_launcher)
                .error(com.network.R.mipmap.ic_launcher).into(view)
        }

        @JvmStatic
        @BindingAdapter("app:drawableStartFromBoolean")
        fun setDrawableStartFromBoolean(textView: TextView, isLiked: Boolean) {
            val drawableResId = if (isLiked) R.drawable.like else R.drawable.like_2
            val drawable = ContextCompat.getDrawable(textView.context, drawableResId)
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }
}