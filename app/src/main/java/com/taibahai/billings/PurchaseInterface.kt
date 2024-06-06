package com.taibahai.billings

interface PurchaseInterface {
    fun onPurchaseUpdate(position: Int, type: String? = "", data: Any? = null) {}
}