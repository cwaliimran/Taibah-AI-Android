package com.taibahai.billings

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.google.common.collect.ImmutableList
import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.productsList
import com.network.utils.AppConstants
import com.taibahai.BuildConfig
import com.taibahai.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class BillingClientManager(
    private val activity: Activity,
    purchasesUpdatedListener: PurchasesUpdatedListener,
    private val actions: BillingManagerActions,
    private val purchaseListener: PurchaseInterface,
    private val productsInterface: ProductsInterface
) {
    private val TAG = "BillingClientManager"
    private var currentPurchasedItemSentToServer = false
    lateinit var productToBuy: ProductDetails
    private val purchaseScope = CoroutineScope(Dispatchers.Main)
    val pendingPurchasesParams = PendingPurchasesParams.newBuilder().enableOneTimeProducts()
        .build()
    val billingClient: BillingClient =
        BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(pendingPurchasesParams).build()


    fun billingSetup() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(
                billingResult: BillingResult
            ) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "OnBillingSetupFinish connected")
                    //restore automatic disabled
                    queryProduct()
                } else {
                    Log.i(TAG, "OnBillingSetupFinish failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i(TAG, "OnBillingSetupFinish connection lost")
            }
        })

    }

    fun releaseBillingClient() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }


    private fun queryProduct() {
        val productList = ImmutableList.builder<QueryProductDetailsParams.Product>()
            .add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(EnumSubscriptions.TAIBAH_AI_GOLD.productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
            .add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(EnumSubscriptions.TAIBAH_AI_SILVER.productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
            .add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(EnumSubscriptions.TAIBAH_AI_DIAMOND.productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
            .build()
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(
            productList
        ).build()

        billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { _, productDetailsList ->

            if (productDetailsList.productDetailsList.isNotEmpty()) {
                //   Log.d(TAG, "onProductDetailsResponse: $productDetailsList")
                productsList = productDetailsList.productDetailsList
                productsInterface.productsFetched(productsList)
            } else {
                Log.d(TAG, "onProductDetailsResponse: No products")
            }
        }
    }


    fun makePurchase(productId: String) {
        productsList.forEach {
            if (it.productId == productId) {
                productToBuy = it
                return@forEach
            }
        }
        if (::productToBuy.isInitialized) { //check if product found
            currentPurchasedItemSentToServer = false
            val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
                ImmutableList.of(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productToBuy)
                        .setOfferToken(productToBuy.subscriptionOfferDetails?.get(0)?.offerToken.toString())
                        .build()
                )
            ).build()
            billingClient.launchBillingFlow(activity, billingFlowParams)
        } else {
            //product not found
            purchaseListener.onPurchaseUpdate(
                0,
                "purchase_no_product",
                EnumPurchaseResponse.PRODUCT_NOT_FOUND
            )
        }
    }


    fun completePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) purchaseScope.launch {
            Log.d(TAG, "completePurchase: calling API")
            Log.d(TAG, "completePurchase: ${purchase.products}")
            var purchasedProductId = ""
            purchase.products.forEach {
                Log.d(TAG, "completePurchase: $it")
                purchasedProductId = it
            }
            if (!currentPurchasedItemSentToServer) {
                currentPurchasedItemSentToServer = true

                consumePurchaseNow(purchase.purchaseToken)

                //acknowledge purchase
                if (!purchase.isAcknowledged) {
                    purchaseScope.launch {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                        val ackPurchaseResult = withContext(Dispatchers.IO) {
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                        }
                        Log.d(
                            TAG, "completePurchase Acknowledge: ${ackPurchaseResult.debugMessage}"
                        )
                    }
                }

                purchaseListener.onPurchaseUpdate(
                    0, "purchase", purchasedProductId
                )

            } else {
                Log.d(TAG, "completePurchase: API already CALLED")
            }
        }
    }


    fun restorePurchase() {
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()

        billingClient.queryPurchasesAsync(
            queryPurchasesParams, restorePurchasesListener
        )
    }

    private val restorePurchasesListener = PurchasesResponseListener { billingResult, purchases ->
        if (purchases.isNotEmpty()) {
            //val purchase = purchases.first()
            activity.runOnUiThread {
                activity.showToast("Completed Successfully!")
                Log.d(TAG, "restorePurchasesListener on $TAG: ")
            }
            purchases.forEachIndexed { index, purchase ->
                Log.d(TAG, "purchases restore: " + purchase.toString())
                consumePurchases() //check if its really needed because we consume every time user buys it, so it would be already consumed
                showMsgAndSaveInDb(purchase.products)
            }

        } else {
            activity.runOnUiThread {
                activity.showToast("No subscription found!")
            }
            Log.d(TAG, "PurchasesResponseListener: not purchased")
        }
    }

    private fun showMsgAndSaveInDb(products: MutableList<String>) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                repeat(products.size) {
                    purchaseListener.onPurchaseUpdate(
                        0, "restore", products
                    )
                }
            }
        }
    }

    //consumes all products
    fun consumePurchases() {
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()

        billingClient.queryPurchasesAsync(
            queryPurchasesParams
        ) { _, purchases ->
            if (purchases.isNotEmpty()) {
                purchases.forEachIndexed { index, purchase ->
                    Log.d(TAG, "purchases restore: " + purchase.toString())
                    consumePurchaseNow(purchase.purchaseToken)
                }

            } else {
                activity.runOnUiThread {
                    // showToast("No purchase found!")
                }
                Log.d(TAG, "PurchasesResponseListener: not purchased")
            }
        }
    }


    private fun consumePurchaseNow(token: String) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(token).build()
        val listener = ConsumeResponseListener { billingResult, purchaseToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                activity.runOnUiThread {
                    // showToast("Purchase consumed")
                    Log.d(TAG, "consumePurchaseNow: ")
                    //clearLocalDatabase()
                }
            }
        }
        billingClient.consumeAsync(consumeParams, listener)
    }

    fun checkSubscriptionStatusFromManager() {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val activeSubscriptions = purchases.filter {
                    it.purchaseState == Purchase.PurchaseState.PURCHASED &&
                            it.products.any { purchasedId -> productsList.any { p -> p.productId == purchasedId } }
                }

                Log.d(TAG, "Active subscriptions: $activeSubscriptions")

                if (activeSubscriptions.isNotEmpty()) {
                    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                    val awardedMonth = AppClass.sharedPref.getInt("tokens_awarded_month")

                    activeSubscriptions.forEach { subscription ->
                        val startTime = subscription.purchaseTime
                        val endTime = calculateExpiryTime(subscription)

                        AppClass.sharedPref.storeLong("subscription_start_date", startTime)
                        AppClass.sharedPref.storeLong("subscription_end_date", endTime)

                        if (awardedMonth != currentMonth) {
                            awardTokens(subscription.products)
                            AppClass.sharedPref.storeInt("tokens_awarded_month", currentMonth)
                        }
                    }
                } else {
                    Log.d(TAG, "No active subscriptions found.")
                    if (BuildConfig.FLAVOR == "prod") {
                        revokeTokensAndLevels()
                    }
                }
            } else {
                Log.e(TAG, "Failed to query subscriptions: ${billingResult.debugMessage}")
            }
        }
    }
    private fun calculateExpiryTime(purchase: Purchase): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = purchase.purchaseTime

        purchase.products.forEach { productId ->
            when (productId) {
                EnumSubscriptions.TAIBAH_AI_SILVER.productId -> {
                    calendar.add(Calendar.MONTH, 1)
                }

                EnumSubscriptions.TAIBAH_AI_GOLD.productId -> {
                    calendar.add(Calendar.MONTH, 1)
                }

                EnumSubscriptions.TAIBAH_AI_DIAMOND.productId -> {
                    calendar.add(Calendar.MONTH, 1)
                }
            }
        }

        return calendar.timeInMillis
    }


    private fun awardTokens(products: List<String>) {
        var aiTokens = AppClass.sharedPref.getInt(AppConstants.AI_TOKENS)

        products.forEach {
            when (it) {
                EnumSubscriptions.TAIBAH_AI_SILVER.productId -> {
                    aiTokens += 300
                }

                EnumSubscriptions.TAIBAH_AI_GOLD.productId -> {
                    aiTokens += 700
                }

                EnumSubscriptions.TAIBAH_AI_DIAMOND.productId -> {
                    aiTokens += 100000
                }
            }
        }

        AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, aiTokens)
    }

    private fun revokeTokensAndLevels() {
        if (AppClass.sharedPref.getInt(AppConstants.AI_TOKENS) > 30) {
            AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, 0)
        }
        AppClass.sharedPref.storeBoolean(AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED, false)
        AppClass.sharedPref.storeBoolean(AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED, false)
        AppClass.sharedPref.storeBoolean(AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED, false)
        AppClass.sharedPref.storeBoolean(AppConstants.IS_ADS_FREE, false)
    }

}