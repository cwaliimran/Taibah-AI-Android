package com.network.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.network.models.ModelUser
import java.util.Locale


public class AppClass : Application() {
    val BASE_URL_1 = "https://taibahislamic.com/admin/"

    override fun onCreate() {
        super.onCreate()
        myApp = this
        sharedPref = SharedPref(this)
        createNotificationChannel()
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
        private const val TAG = "AppClass"
        var myApp: AppClass? = null
        val instance get() = myApp!!
        lateinit var sharedPref: SharedPref

        //for storing data
        //    AppClass.sharedPref?.storeObject(AppConstants.CURRENT_USER, responseData.body)
        fun getCurrentUser(): ModelUser? {
            return sharedPref.getObject(AppConstants.CURRENT_USER, ModelUser::class.java)
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

    }
}