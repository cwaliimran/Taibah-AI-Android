package com.taibahai.billings

import com.taibahai.models.InAppPurchase

interface BillingManagerActions {
    fun addInAppPurchase(inAppPurchase: InAppPurchase)
}