package com.network.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.network.R
import com.network.models.ModelUser
import java.io.File
import java.util.Locale


public class AppClass : Application() {
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


    //    public static void deleteFile(String path){
    //        File fdelete = new File(uri.getPath());
    //        if (fdelete.exists()) {
    //            if (fdelete.delete()) {
    //                System.out.println("file Deleted :" + uri.getPath());
    //            } else {
    //                System.out.println("file not Deleted :" + uri.getPath());
    //            }
    //        }
    //
    //
    //        File file = new File(path);
    //        file.delete();
    //        if(file.exists()){
    //            file.getCanonicalFile().delete();
    //            if(file.exists()){
    //                getApplicationContext().deleteFile(file.getName());
    //            }
    //        }
    //    }


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


        fun getAccessToken(): String? {
            return sharedPref.getString(AppConstants.ACCESS_TOKEN, "")
        }

        fun changeLocale(context: Context, locale: String? = "en") {
            val res: Resources = context.resources
            val conf: Configuration = res.configuration
            conf.setLocale(Locale(locale))
            res.updateConfiguration(conf, res.displayMetrics)
        }

        fun progressToTimer(i: Int, i2: Int): Int {
            return (i.toDouble() / 100.0 * (i2 / 1000).toDouble()).toInt() * 1000
        }

        fun getProgressPercentage(j: Long, j2: Long): Int {
            java.lang.Double.valueOf(0.0)
            return java.lang.Double.valueOf(
                (j / 1000).toInt().toLong().toDouble() / (j2 / 1000).toInt().toLong()
                    .toDouble() * 100.0
            ).toInt()
        }

        fun getTimeString(duration: Int): String? {
            val minutes = Math.floor((duration / 1000 / 60).toDouble()).toInt()
            val seconds = (duration / 1000 - minutes * 60).toInt()
            return minutes.toString() + ":" + String.format("%02d", seconds)
        }

        fun getAudioOutputDirectory(): File? {
            val mediaStorageDir: File = File(
                AppClass.instance.getFilesDir().toString() + "/" +
                        AppClass.instance.getString(R.string.app_name) + "/Audios"
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

    }
}