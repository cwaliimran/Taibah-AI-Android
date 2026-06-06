package com.network.billings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.ProgressBar
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

object RewardedInterstitialAdManager {
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isLoading = false
    private var isShowing = false
    private var clickCounter = 0

    var adShowFrequency = 3

    private val adUnitId: String
        get() = "ca-app-pub-3940256099942544/5354046379"

    fun loadAd(context: Context) {
        if (rewardedInterstitialAd != null || isLoading) return
        isLoading = true
        RewardedInterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedInterstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    fun showAdIfEligible(
        activity: Activity,
        isAdsFree: Boolean,
        onNextAction: () -> Unit
    ) {
        if (isShowing) return

        if (isAdsFree) {
            onNextAction()
            return
        }

        clickCounter++

        if (clickCounter >= adShowFrequency) {
            if (rewardedInterstitialAd != null) {
                displayAd(activity, onNextAction)
            } else {
                forceLoadAndDisplay(activity, onNextAction)
            }
        } else {
            onNextAction()
        }
    }

    private fun displayAd(activity: Activity, onNextAction: () -> Unit) {
        val ad = rewardedInterstitialAd ?: return
        isShowing = true
        clickCounter = 0

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedInterstitialAd = null
                isShowing = false
                loadAd(activity)
                onNextAction()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedInterstitialAd = null
                isShowing = false
                loadAd(activity)
                onNextAction()
            }
        }
        ad.show(activity) { }
    }

    private fun forceLoadAndDisplay(activity: Activity, onNextAction: () -> Unit) {
        val progressBar = ProgressBar(activity)
        progressBar.setPadding(0, 50, 0, 50)

        val dialog = AlertDialog.Builder(activity)
            .setTitle("Loading Ad...")
            .setView(progressBar)
            .setCancelable(false)
            .create()

        if (!activity.isFinishing && !activity.isDestroyed) {
            dialog.show()
        }

        RewardedInterstitialAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    if (!activity.isFinishing && !activity.isDestroyed) {
                        dialog.dismiss()
                    }
                    rewardedInterstitialAd = ad
                    displayAd(activity, onNextAction)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    if (!activity.isFinishing && !activity.isDestroyed) {
                        dialog.dismiss()
                    }
                    rewardedInterstitialAd = null
                    clickCounter = adShowFrequency
                    onNextAction()
                }
            }
        )
    }
}