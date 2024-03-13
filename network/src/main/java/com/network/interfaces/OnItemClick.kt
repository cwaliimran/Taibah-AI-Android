package com.network.interfaces

import android.view.View

interface OnItemClick {
    fun onClick(position: Int, type: String? = "", data: Any? = null, view: View? = null) {}
}