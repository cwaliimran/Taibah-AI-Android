package com.network.interfaces

interface AlertDialogListener {
    fun onYesClick(data : Any? = null)
    fun onNoClick(data: Any? = null){}
}