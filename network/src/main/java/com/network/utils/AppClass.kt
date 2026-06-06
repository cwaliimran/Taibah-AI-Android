package com.network.utils

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.billingclient.api.ProductDetails
import com.google.android.gms.ads.MobileAds
import com.network.BuildConfig
import com.network.billings.AppOpenAdManager
import com.network.billings.RewardedInterstitialAdManager
import com.network.models.ModelUser
import java.io.File
import java.util.Locale

class AppClass : Application(), Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    var singleton: AppClass? = null
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super<Application>.onCreate()
        myApp = this
        singleton = this
        sharedPref = SharedPref(this)
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        createNotificationChannel()
        com.google.android.gms.ads.MobileAds.initialize(this) {

        }
        AppOpenAdManager.loadAd(this)

    }

    override fun onStart(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onStart(owner)
        currentActivity?.let {
            AppOpenAdManager.showAdIfAvailable(it, isAdsFreeUser())
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        if (!AppOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (!AppOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    fun getInstance(): AppClass? {
        return singleton
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Taibah AI Alerts"
            val channelName = "Taibah AI Alerts"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        val BASE_URL_1 = "https://taibahislamic.com/"
        private const val TAG = "AppClass"
        var myApp: AppClass? = null
        val instance get() = myApp!!
        lateinit var sharedPref: SharedPref

        fun getCurrentUser(): ModelUser.Data? {
            return sharedPref.getObject(AppConstants.CURRENT_USER, ModelUser.Data::class.java)
        }

        fun isGuest(): Boolean {
            return if (BuildConfig.FLAVOR == "adsFree") {
                getCurrentUser()?.social_type == "admin"
            } else {
                getCurrentUser()?.social_type == "guest"
            }
        }

        fun isAdsFreeUser(): Boolean {
            val isPurchased = sharedPref.getBoolean(AppConstants.IS_ADS_FREE)
            return com.network.BuildConfig.FLAVOR == "adsFree" || isPurchased
        }

        fun getAccessToken(): String? {
            return sharedPref.getString(AppConstants.ACCESS_TOKEN, "")
        }

        fun changeLocale(context: Context, locale: String? = "en") {
            val res: Resources = context.resources
            val conf: Configuration = res.configuration
            conf.setLocale(Locale(locale))
            res.updateConfiguration(conf, res.displayMetrics)
        }

        fun getAudioOutputDirectory(): File {
            val mediaStorageDir: File = File(
                instance.filesDir
                    .toString() + "/" + "TaibahAI" + "/Audios"
            )
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
            return mediaStorageDir
        }

        fun isFileExists(childPath: String?): Boolean {
            val yourFile = File(getAudioOutputDirectory(), childPath)
            return yourFile.exists()
        }

        fun deleteFile(filePath: String?, context: Context?): Boolean {
            val dir = context!!.filesDir
            val file = File(dir, filePath)
            return file.delete()
        }

        fun getTimeString(duration: Long): String {
            val minutes = Math.floor((duration / 1000 / 60).toDouble()).toInt()
            val seconds = (duration / 1000 - minutes * 60).toInt()
            return minutes.toString() + ":" + String.format("%02d", seconds)
        }

        var productsList: MutableList<ProductDetails> = mutableListOf()
    }
}