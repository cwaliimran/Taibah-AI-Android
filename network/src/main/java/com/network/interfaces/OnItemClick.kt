package com.network.interfaces

interface OnItemClick {
    fun onClick(position: Int, type: String? = "", data: Any? = null) {}

 /*   fun onDownload(position: Int, type: String? = "", data: Any? = null) {}
    fun onDelete(position: Int, type: String? = "", data: Any? = null) {}
    fun onPause(position: Int, type: String? = "", data: Any? = null) {}
    fun onResume(position: Int, type: String? = "", data: Any? = null) {}
    fun onRetryDownload(position: Int, type: String? = "", data: Any? = null)*/

}