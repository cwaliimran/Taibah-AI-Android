package com.network.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import com.network.utils.AppClass
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*

object NetworkUtils {
    fun isInternetAvailable(): Boolean {
        val context = AppClass.instance.applicationContext
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            ) ?: false
        }
    }
    private var sb: Snackbar? = null
    fun showSnackBar(view: View?) {
        sb = Snackbar.make(
            view!!,
            "please check your internet connection",
            BaseTransientBottomBar.LENGTH_LONG
        )
        /*     sb!!.setAction("Ok"
             ) { sb!!.dismiss() }*/
        sb!!.show()
    }

    fun hideSnackBar(view: View?) {
        sb?.dismiss()
    }

    //sample format Asia/Karachi
    fun timeZone(): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone
        return tz.id
    }


}