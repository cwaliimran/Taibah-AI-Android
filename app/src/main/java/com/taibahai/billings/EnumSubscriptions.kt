package com.taibahai.billings

enum class EnumSubscriptions(val productId: String) {
    TAIBAH_AI_SILVER("taibah_ai_silver"),
    TAIBAH_AI_GOLD("taibah_ai_gold"),
    TAIBAH_AI_DIAMOND("taibah_ai_diamond"),
}

enum class EnumPurchaseResponse {
    PURCHASE_SUCCESS,
    PURCHASE_RESTORE,
    PRODUCT_NOT_FOUND,
    PURCHASE_FAIL,
}

enum class EnumSubscriptionStatus {
    SUBSCRIPTION_ACTIVE,       // User has an active subscription
    SUBSCRIPTION_INACTIVE,     // User does not have an active subscription
    SUBSCRIPTION_ERROR         // Error occurred while checking subscription status
}