package com.network.billings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.ProgressBar
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

object AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    private var loadTime: Long = 0
    private var openCount = 0

    var adShowFrequency = 3
    private const val TAG = "AppOpenAdDebug"

    private val adUnitId: String
        get() = "ca-app-pub-3940256099942544/9257395921"

    fun loadAd(context: Context) {
        Log.d(TAG, "loadAd() called | isLoadingAd=$isLoadingAd | isAdAvailable=${isAdAvailable()}")

        if (isLoadingAd || isAdAvailable()) return

        isLoadingAd = true
        val request = AdRequest.Builder().build()

        AppOpenAd.load(
            context,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.e(
                        TAG,
                        "onAdFailedToLoad() code=${loadAdError.code} message=${loadAdError.message}"
                    )
                }
            }
        )
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun displayAd(activity: Activity) {
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        appOpenAd?.show(activity)
    }

    fun showAdIfAvailable(activity: Activity, isAdsFree: Boolean) {
        Log.d(
            TAG,
            "showAdIfAvailable() called | isShowingAd=$isShowingAd | isAdsFree=$isAdsFree | openCount=$openCount"
        )
        if (isShowingAd) {
            Log.d(TAG, "RETURN -> Ad already showing")
            return
        }

        if (isAdsFree) {
            Log.d(TAG, "RETURN -> Ads Free User")
            return
        }

        openCount++

        Log.d(TAG, "Counter Incremented -> openCount=$openCount")

        if (openCount < adShowFrequency) {
            Log.d(
                TAG,
                "Frequency not reached -> openCount=$openCount frequency=$adShowFrequency"
            )

            loadAd(activity)
            return
        }
        Log.d(
            TAG,
            "Frequency reached -> openCount=$openCount adAvailable=${isAdAvailable()}"
        )

        if (isAdAvailable()) {
            Log.d(TAG, "Showing Loaded Ad")
            openCount = 0
            displayAd(activity)
        } else {
            Log.d(TAG, "Ad NOT Available -> Loading Fresh Ad")
            val progressBar = ProgressBar(activity).apply {
                setPadding(50, 50, 50, 50)
            }
            val dialog = AlertDialog.Builder(activity)
                .setCancelable(false)
                .setView(progressBar)
                .create()

            dialog.show()
            isLoadingAd = true

            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                activity,
                adUnitId,
                request,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                        dialog.dismiss()
                        openCount = 0
                        displayAd(activity)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                        dialog.dismiss()
                        openCount = 0
                    }
                }
            )
        }
    }
}