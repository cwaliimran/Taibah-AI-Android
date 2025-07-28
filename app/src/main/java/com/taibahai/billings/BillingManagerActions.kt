package com.taibahai.billings

import com.android.billingclient.api.Purchase
import com.taibahai.models.InAppPurchase

interface BillingManagerActions {
    fun addInAppPurchase(inAppPurchase: InAppPurchase)
    fun onSubscriptionActive(subscriptions: List<Purchase>)
    fun onSubscriptionInactive()
    fun onSubscriptionError(message: String)
}