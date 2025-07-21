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
import com.network.utils.AppClass.Companion.productsList
import com.taibahai.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .build()
    val billingClient: BillingClient =
        BillingClient.newBuilder(activity).setListener(purchasesUpdatedListener)
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
            purchaseListener.onPurchaseUpdate(0, "purchase_no_product", EnumPurchaseResponse.PRODUCT_NOT_FOUND)
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

    ///////////////////////////////////////////////////////////////////////////
    // CHECK SUBSCRIPTION STATUS
    ///////////////////////////////////////////////////////////////////////////
    fun checkSubscriptionStatus() {
        // Create a query to check the user's active subscriptions.
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()

        billingClient.queryPurchasesAsync(
            queryPurchasesParams
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Query was successful, check the subscription status.
                val activeSubscriptions =
                    purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }

                if (activeSubscriptions.isNotEmpty()) {
                    // The user has active subscriptions.
                    // You can now check the subscription expiration dates, etc.
                    // Implement the logic to check the subscription status and expiration date here.

                    // For example, you can check the expiration date of the first active subscription:
                    activeSubscriptions.forEach { subscription ->
                        Log.d(TAG, "checkSubscriptionStatus: $subscription")
                        Log.d(TAG, "checkSubscriptionStatus: ${subscription.products}")
                        subscription.products.forEach {
                            Log.d(TAG, "checkSubscriptionStatus: $it")
                            if (it == EnumSubscriptions.TAIBAH_AI_SILVER.productId) {
                                purchaseListener.onPurchaseUpdate(
                                    0, "subscription_status", EnumSubscriptionStatus.SUBSCRIPTION_ACTIVE
                                )

                            }
                            if (it == EnumSubscriptions.TAIBAH_AI_GOLD.productId) {
                                purchaseListener.onPurchaseUpdate(
                                    0, "subscription_status", EnumSubscriptionStatus.SUBSCRIPTION_ACTIVE
                                )
                            }
                            if (it == EnumSubscriptions.TAIBAH_AI_DIAMOND.productId) {
                                purchaseListener.onPurchaseUpdate(
                                    0, "subscription_status", EnumSubscriptionStatus.SUBSCRIPTION_ACTIVE
                                )
                            }

                        }

                    }
                } else {
                    // No active subscriptions found.
                    // Handle this scenario accordingly, e.g., prompt the user to subscribe.

                    // Notify your UI or application logic about the subscription status.
                    purchaseListener.onPurchaseUpdate(
                        0, "subscription_status", EnumSubscriptionStatus.SUBSCRIPTION_INACTIVE
                    )
                }
            } else {
                // Handle the case where the query fails.
                Log.e(TAG, "Failed to query active subscriptions: ${billingResult.debugMessage}")

                // Notify your UI or application logic about the error.
                purchaseListener.onPurchaseUpdate(
                    0, "subscription_status", EnumSubscriptionStatus.SUBSCRIPTION_ERROR
                )
            }
        }
    }

}