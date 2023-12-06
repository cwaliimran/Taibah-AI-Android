package com.network.network

import android.content.Context
import android.util.Log
import com.network.R
import com.network.network.NetworkUtils.isInternetAvailable
import com.network.utils.AppClass
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NetworkInterceptor(private val context: Context) : Interceptor {
    private val TAG = "NetworkInterceptorTAG"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //throw exception if no internet connection available
        if (!isInternetAvailable()) {
            throw NetworkException(context.getString(R.string.message_no_internet_connection))
        }
        val token = AppClass.getAccessToken().toString()
        var request: Request = chain.request()
        request = if (token == "null") {
            Log.d(TAG, "intercept: nulltoken")
            request.newBuilder().addHeader(
                    "Accept", "application/json"
                ) //                .addHeader("Request-Type", "Android")
                .addHeader("Content-Type", "application/json").build()
        } else {
            Log.d(TAG, "intercept: with token $token")
            request.newBuilder().addHeader(
                    "Accept", "application/json"
                ) //                .addHeader("Request-Type", "Android")
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Content-Type", "application/json")
                .addHeader("accesstoken", token).build()
        }
        return chain.proceed(request)
    }
}

class NetworkException(override var message: String) : IOException(
    message
)