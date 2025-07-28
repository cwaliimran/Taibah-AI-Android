package com.taibahai.bottom_navigation

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.taibahai.R
import com.taibahai.billings.BillingClientManager
import com.taibahai.billings.BillingManagerActions
import com.taibahai.billings.ProductsInterface
import com.taibahai.billings.PurchaseInterface
import com.taibahai.databinding.ActivityBottomNavigationBinding
import com.taibahai.fragments.ExploreFragment
import com.taibahai.fragments.HomeFragment
import com.taibahai.fragments.MoreFragment
import com.taibahai.fragments.SearchFragment
import com.taibahai.models.InAppPurchase

class BottomNavigation : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavigationBinding
    private val TAG = "BottomNavigation"

    //    private lateinit var billingClient: BillingClient
    lateinit var billingClientManager: BillingClientManager

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

            billingClientManager = BillingClientManager(
                this,
                purchasesUpdatedListener = object : PurchasesUpdatedListener {
                    override fun onPurchasesUpdated(
                        billingResult: BillingResult, purchases: MutableList<Purchase>?
                    ) {
                        Log.d(
                            TAG,
                            "onPurchasesUpdated: BillingResult - ${billingResult.debugMessage}, Purchases - $purchases"
                        )
                    }
                },
                actions = object : BillingManagerActions { /* optional */
                    override fun addInAppPurchase(inAppPurchase: InAppPurchase) {
                        Log.d(TAG, "addInAppPurchase: $inAppPurchase")
                    }

                    override fun onSubscriptionActive(subscriptions: List<Purchase>) {
                        Log.d(TAG, "onSubscriptionActive: Subscriptions - $subscriptions")
                        billingClientManager.checkSubscriptionStatusFromManager()
                    }

                    override fun onSubscriptionInactive() {
                        Log.d(TAG, "onSubscriptionInactive: No active subscriptions")
                    }

                    override fun onSubscriptionError(message: String) {
                        Log.e(TAG, "onSubscriptionError: $message")
                    }
                },
                purchaseListener = object : PurchaseInterface { /* handle updates */ },
                productsInterface = object : ProductsInterface { /* fetch product list */ })
            billingClientManager.billingSetup()
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
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
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
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        super.onDestroy()
        billingClientManager.releaseBillingClient()
    }

}