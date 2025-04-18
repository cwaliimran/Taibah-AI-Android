package com.network.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.android.billingclient.api.ProductDetails
import com.network.BuildConfig
import com.network.models.ModelUser
import java.io.File
import java.util.Locale


class AppClass : Application() {
    var singleton: AppClass? = null


    override fun onCreate() {
        super.onCreate()
        myApp = this
        AppClass().singleton = this
        sharedPref = SharedPref(this)
        createNotificationChannel()
    }

    fun getInstance(): AppClass? {
        return AppClass().singleton
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
        val BASE_URL_1 = "https://taibahislamic.com/admin/"

        private const val TAG = "AppClass"
        var myApp: AppClass? = null
        val instance get() = myApp!!
        lateinit var sharedPref: SharedPref

        //for storing data
        //    AppClass.sharedPref?.storeObject(AppConstants.CURRENT_USER, responseData.body)
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