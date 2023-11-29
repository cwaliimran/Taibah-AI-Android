package com.network.utils

import android.content.res.Resources
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior


fun showBottomSheet(bottomSheet: FrameLayout, isExpanded : Boolean?=false) {
    val layoutParams = bottomSheet.layoutParams
    layoutParams.height = Resources.getSystem().displayMetrics.heightPixels
    bottomSheet.setBackgroundResource(android.R.color.transparent)
    bottomSheet.layoutParams = layoutParams
    if (isExpanded==true){
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        expandBottomSheet(bottomSheetBehavior)
    }
}

 fun expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<FrameLayout>) {
    bottomSheetBehavior.skipCollapsed = true
    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
}