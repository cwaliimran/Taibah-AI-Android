package com.taibahai.bottom_navigation

import android.content.IntentSender
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.taibahai.BuildConfig
import com.taibahai.R
import com.taibahai.billings.EnumSubscriptions
import com.taibahai.databinding.ActivityBottomNavigationBinding
import com.taibahai.fragments.ExploreFragment
import com.taibahai.fragments.HomeFragment
import com.taibahai.fragments.MoreFragment
import com.taibahai.fragments.SearchFragment
import java.util.Calendar

class BottomNavigation : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavigationBinding
    private val TAG = "BottomNavigation"
    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView?.itemIconTintList = null
        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.explore -> replaceFragment(ExploreFragment())
                R.id.more -> replaceFragment(MoreFragment())
                else -> {}
            }
            true
        }

        checkForAppUpdate()
       try {
           val pendingPurchasesParams = PendingPurchasesParams.newBuilder().build()

           billingClient = BillingClient.newBuilder(this@BottomNavigation)
               .setListener(object : PurchasesUpdatedListener {
                   override fun onPurchasesUpdated(
                       billingResult: BillingResult,
                       purchases: MutableList<Purchase>?
                   ) {
                       Log.d(TAG, "onPurchasesUpdated TOP: $purchases")
                   }
               })
               .enablePendingPurchases(pendingPurchasesParams)
               .build()

           billingClient.startConnection(object : BillingClientStateListener {
               override fun onBillingSetupFinished(billingResult: BillingResult) {
                   if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                       Log.i(TAG, "OnBillingSetupFinish connected")
                       // Check subscription status after billing setup is finished
                       checkSubscriptionStatus()
                   } else {
                       Log.i(
                           TAG,
                           "OnBillingSetupFinish failed with code: ${billingResult.responseCode}"
                       )
                   }
               }

               override fun onBillingServiceDisconnected() {
                   Log.i(TAG, "OnBillingServiceDisconnected")
               }
           })
       } catch (e: Exception) {
           Log.e(TAG, "Error initializing BillingClient: ${e.message}")
       }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun checkForAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            Log.d("TAG", result.toString())
        }

    override fun onResume() {
        super.onResume()
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

  override fun onDestroy() {
      super.onDestroy()
      if (::billingClient.isInitialized && billingClient.isReady) {
          billingClient.endConnection()
      }
  }


    private fun checkSubscriptionStatus() {
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()

        billingClient.queryPurchasesAsync(
            queryPurchasesParams
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val activeSubscriptions =
                    purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                Log.d(TAG, "checkSubscriptionStatus activeSubscriptions: $activeSubscriptions")

                if (activeSubscriptions.isNotEmpty()) {
                    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                    val awardedMonth = AppClass.sharedPref.getInt("tokens_awarded_month")

                    activeSubscriptions.forEach { subscription ->
                        val startTime = subscription.purchaseTime
                        val endTime = calculateExpiryTime(subscription) // Calculate the expiry time

                        AppClass.sharedPref.storeLong("subscription_start_date", startTime)
                        AppClass.sharedPref.storeLong("subscription_end_date", endTime)

                        if (awardedMonth != currentMonth) {
                            awardTokens(subscription.products)
                            AppClass.sharedPref.storeInt("tokens_awarded_month", currentMonth)
                        }
                    }
                } else {
                    Log.d(TAG, "checkSubscriptionStatus: not active")
                    if (BuildConfig.FLAVOR == "prod") {
                        revokeTokensAndLevels()
                    }
                }
            } else {
                Log.e(TAG, "Failed to query active subscriptions: ${billingResult.debugMessage}")
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