package com.taibahai.utils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.taibahai.R
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

object AppTourDialog {
    fun  appTour(context:Activity, view: View, title:String, description:String,
         color: Int = ContextCompat.getColor(context, R.color.primary_green),
                 onSelect: (Boolean) -> Unit) {
        MaterialTapTargetPrompt.Builder(context)
            .setTarget(view)
            .setPrimaryText(title)
            .setSecondaryText(description) // this is used when user click on Focal it
            // should do some functions so we are adding toast
            .setPromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                    onSelect(true)
                } else if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    onSelect(true)
                }
            }
            .setBackgroundColour(
                color
            )
            .setFocalColour(
                ContextCompat.getColor(context, R.color.gray)
            )
            .setCaptureTouchEventOutsidePrompt(true) // this is used when user click on Focal it
            // should do some functions so we are adding toast
            //adjust size
            .setFocalRadius(100f)
            .show()
    }
}