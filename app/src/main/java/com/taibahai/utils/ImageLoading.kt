package com.taibahai.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.taibahai.R

object ImageLoading {

    fun Context.loadImageWithProgress(imageUrl: String, imageView: ImageView, progress: View) {
        if (imageUrl.isEmpty()) {
            // If the image URL is null or empty, don't show the progress bar and return from the function
            imageView.setImageResource(R.drawable.placeholder)
            progress.visibility = View.GONE
            return
        }
        imageView.visibility = View.VISIBLE
        // Show the progress bar before loading the image
        progress.visibility = View.VISIBLE
        Glide.with(this)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Image loading failed so hide progress bar
                    progress.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Image was loaded successfully, hide the progress bar
                    progress.visibility = View.GONE
                    return false
                }


            })
            .into(imageView)

    }

    //trying to achieve progressbar programmatically
    fun Context.loadImageWithProgress1(imageUrl: String, imageView: ImageView) {
        val progress = ProgressBar(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.progress_bar_width),
                resources.getDimensionPixelSize(R.dimen.progress_bar_height)
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            indeterminateTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_green))
        }
        imageView.visibility = View.VISIBLE


        // Add the progress bar to the parent view of the image view
        (imageView.parent as? ViewGroup)?.addView(progress)

        if (imageUrl.isEmpty()) {
            // If the image URL is null or empty, load a placeholder image and return
            imageView.setImageResource(R.drawable.placeholder)
            progress.visibility = View.GONE
            return
        }

        progress.visibility = View.VISIBLE

        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Image loading failed so hide progress bar
                    progress.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Image was loaded successfully, hide the progress bar
                    progress.visibility = View.GONE
                    return false
                }


            })
            .into(imageView)
    }


}