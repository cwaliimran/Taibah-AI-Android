package com.taibahai.activities


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.network.base.BaseActivity
import com.network.interfaces.AlertDialogListener
import com.network.interfaces.OnItemClick
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.taibahai.R
import com.taibahai.adapters.AdapterUpgrade
import com.taibahai.billings.BillingClientManager
import com.taibahai.billings.BillingManagerActions
import com.taibahai.billings.EnumSubscriptions
import com.taibahai.billings.ProductsInterface
import com.taibahai.billings.PurchaseInterface
import com.taibahai.databinding.ActivityUpgradeBinding
import com.taibahai.databinding.DialogPurchasesBinding
import com.taibahai.models.InAppPurchase
import com.taibahai.models.ModelUpgrade
import com.taibahai.models.ModelUpgradeList
import com.taibahai.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpgradeActivity : BaseActivity(), PurchaseInterface {
    lateinit var binding: ActivityUpgradeBinding
    lateinit var adapter: AdapterUpgrade
    val showList = ArrayList<ModelUpgrade>()

    private lateinit var billingClientManager: BillingClientManager
    private val TAG = "SubscriptionsActivity"

    override fun onCreate() {
        binding = ActivityUpgradeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addViewPager()
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()

        val upgradeBasic = ArrayList<ModelUpgradeList>()
        upgradeBasic.add(ModelUpgradeList("300 AI Tokens/Month"))
        upgradeBasic.add(ModelUpgradeList("Ads"))
        upgradeBasic.add(ModelUpgradeList("Quran"))
        upgradeBasic.add(ModelUpgradeList("Hadith"))
        upgradeBasic.add(ModelUpgradeList("Zakat Calculator"))

        showList.add(ModelUpgrade("1", "Basic", "Silver Package", upgradeBasic, "\$2.99/month", isSilverPurchased))


        val upgradeAdvance = ArrayList<ModelUpgradeList>()
        upgradeAdvance.add(ModelUpgradeList("700 AI Tokens/Month"))
        upgradeAdvance.add(ModelUpgradeList("No Ads"))
        upgradeAdvance.add(ModelUpgradeList("Quran"))
        upgradeAdvance.add(ModelUpgradeList("Hadith"))
        upgradeAdvance.add(ModelUpgradeList("Zakat Calculator"))
        upgradeAdvance.add(ModelUpgradeList("4 Influential Scholars"))

        showList.add(ModelUpgrade("2", "Advance", "Gold Package", upgradeAdvance, "\$4.99/month", isGoldPurchased))


        val upgradeExclusive = ArrayList<ModelUpgradeList>()
        upgradeExclusive.add(ModelUpgradeList("Unlimited AI Tokens/M"))
        upgradeExclusive.add(ModelUpgradeList("No Ads"))
        upgradeExclusive.add(ModelUpgradeList("Quran"))
        upgradeExclusive.add(ModelUpgradeList("Hadith"))
        upgradeExclusive.add(ModelUpgradeList("4 Influential Scholars"))
        upgradeExclusive.add(ModelUpgradeList("Islamic Literature"))
        upgradeExclusive.add(ModelUpgradeList("Zakat Calculator"))
        upgradeExclusive.add(ModelUpgradeList("Inheritance Law"))
        upgradeExclusive.add(ModelUpgradeList("Search Database"))


        showList.add(
            ModelUpgrade(
                "3", "Exclusive", "Diamond Package", upgradeExclusive, "\$9.99/month", isDiamondPurchased
            )
        )
    }


    private fun addViewPager() {
        binding.viewPager.adapter = AdapterUpgrade(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                when (type) {
                    "subscribe" -> {
                        when (position) {
                            0 -> {
                                billingClientManager.makePurchase(EnumSubscriptions.TAIBAH_AI_SILVER.productId)
                            }

                            1 -> {
                                billingClientManager.makePurchase(EnumSubscriptions.TAIBAH_AI_GOLD.productId)
                            }

                            2 -> {
                                billingClientManager.makePurchase(EnumSubscriptions.TAIBAH_AI_DIAMOND.productId)
                            }

                            else -> {}
                        }
                    }

                    "restore" -> {
                        billingClientManager.restorePurchase()
                    }

                    else -> {}
                }
            }
        })
        binding.viewPager.let { binding.dotsIndicator.attachTo(it) }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)


            }
        })

    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Upgrade")


    }


    override fun onPurchaseUpdate(position: Int, type: String?, data: Any?) {
        if (type == "purchase_no_product") {
            this@UpgradeActivity.purchaseDialog(
                object : AlertDialogListener {
                    override fun onYesClick(data: Any?) {

                    }

                },
                getString(R.string.product_not_found),
                getString(R.string.product_not_found_msg),
                hideNoBtn = true
            )
        } else if (type == "purchase") {
            //data = purchasedProductId
            when (data.toString()) {
                EnumSubscriptions.TAIBAH_AI_SILVER.productId -> {
                    AppClass.sharedPref.storeBoolean(
                        AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED, true
                    )
                }

                EnumSubscriptions.TAIBAH_AI_GOLD.productId -> {
                    AppClass.sharedPref.storeBoolean(AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED, true)
                    AppClass.sharedPref.storeBoolean(AppConstants.IS_ADS_FREE, true)

                }

                EnumSubscriptions.TAIBAH_AI_DIAMOND.productId -> {
                    AppClass.sharedPref.storeBoolean(
                        AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED, true
                    )
                    AppClass.sharedPref.storeBoolean(AppConstants.IS_ADS_FREE, true)

                }

                else -> {}
            }
            updateSubscriptionStatus("purchase")
        } else if (type == "restore") {
            val products = data as MutableList<String>
            if (products.isNotEmpty()) {
                products.forEach {
                    when (it) {
                        EnumSubscriptions.TAIBAH_AI_SILVER.productId -> {
                            AppClass.sharedPref.storeBoolean(
                                AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED, true
                            )
                        }

                        EnumSubscriptions.TAIBAH_AI_GOLD.productId -> {
                            AppClass.sharedPref.storeBoolean(
                                AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED,
                                true
                            )

                        }

                        EnumSubscriptions.TAIBAH_AI_DIAMOND.productId -> {
                            AppClass.sharedPref.storeBoolean(
                                AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED, true
                            )
                        }
                    }
                }
            }else{
                runOnMainThread {
                    showToast("No subscription found!")
                }
            }
        }
    }
    private fun runOnMainThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Already on the main thread, run directly
            action()
        } else {
            // Post to the main thread
            Handler(Looper.getMainLooper()).post {
                action()
            }
        }
    }
    override fun apiAndArgs() {
        billingClientManager =
            BillingClientManager(this, purchasesUpdatedListener, object : BillingManagerActions {
                override fun addInAppPurchase(inAppPurchase: InAppPurchase) {
                    // add purchase in db
                 //   updateSubscriptionStatus("restore")
                    Log.d(TAG, "addInAppPurchase restore: $inAppPurchase")
                }
            }, this, object : ProductsInterface {
                override fun productsFetched(products: MutableList<ProductDetails>) {
                    products.forEach {
                        if (it.productId == EnumSubscriptions.TAIBAH_AI_SILVER.productId) {
                            var price =
                                it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                    0
                                )?.formattedPrice

                            if (price.isNullOrBlank()) {
                                price = "\$2.99/month"
                            }
                            showList[0].subscriptionPrice = price
                        }
                        if (it.productId == EnumSubscriptions.TAIBAH_AI_GOLD.productId) {
                            var price =
                                it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                    1
                                )?.formattedPrice

                            if (price.isNullOrBlank()) {
                                price = "\$4.99/month"
                            }
                            showList[0].subscriptionPrice = price
                        }
                        if (it.productId == EnumSubscriptions.TAIBAH_AI_DIAMOND.productId) {
                            var price =
                                it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                    2
                                )?.formattedPrice

                            if (price.isNullOrBlank()) {
                                price = "\$9.99/month"
                            }
                            showList[0].subscriptionPrice = price
                        }
                    }
                }
            })


        billingClientManager.billingSetup()
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.d(TAG, "purchases: $purchases")
            for (purchase in purchases) {
                billingClientManager.completePurchase(purchase)
            }
        }
//        else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
//            //  Log.i(TAG, "onPurchasesUpdated: Purchase Canceled")
//        } else {
//            //  Log.i(TAG, "onPurchasesUpdated: Error ${billingResult.debugMessage}")
//        }
    }


    // Called before the activity is destroyed
    public override fun onDestroy() {
        super.onDestroy()
        billingClientManager.releaseBillingClient()

    }

    private fun updateSubscriptionStatus(type: String) {
        lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        this@UpgradeActivity.purchaseDialog(
                            object : AlertDialogListener {
                                override fun onYesClick(data: Any?) {
                                    startActivity(
                                        Intent(
                                            this@UpgradeActivity, SplashActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                }

                            }, if (type == "purchase") {
                                getString(R.string.purchase_success)
                            } else {
                                getString(R.string.restore_success)
                            }, if (type == "purchase") {
                                getString(R.string.purchase_success_msg)
                            } else {
                                getString(R.string.restore_success_msg)
                            }, hideNoBtn = true
                        )
                    }
                }

        }

    private fun Context.purchaseDialog(
        listener: AlertDialogListener,
        title: String,
        message: String,
        data: Any? = null,
        cancelable: Boolean? = false,
        hideNoBtn: Boolean? = false,
        yesBtnText: String? = getString(R.string.yes),
        noBtnText: String? = getString(R.string.no),
    ) {
        val dialog = Dialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val binding = DialogPurchasesBinding.inflate(layoutInflater)
        binding.tvTitle.text = title
        binding.tvMessage.text = message
        binding.btnyes.text = yesBtnText.toString()
        binding.btnno.text = noBtnText.toString()
        if (hideNoBtn == true) {
            binding.btnno.visibility = View.GONE
            if (yesBtnText == getString(R.string.yes)) {
                binding.btnyes.text = getString(R.string.done)
            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(cancelable!!)
        if (dialog.isShowing) return
        binding.btnno.setOnClickListener {
            dialog.dismiss()
            listener.onNoClick(data)
        }
        binding.btnyes.setOnClickListener {
            dialog.dismiss()
            listener.onYesClick(data)
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }
}