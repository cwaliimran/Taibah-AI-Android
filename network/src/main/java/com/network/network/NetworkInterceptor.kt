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
        val token = AppClass.getAccessToken() ?: ""
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")

        if (token.isNotEmpty() && token != "null") {
            requestBuilder.addHeader("accesstoken", token)
        }

        return chain.proceed(requestBuilder.build())
    }
}

class NetworkException(override var message: String) : IOException(
    message
)