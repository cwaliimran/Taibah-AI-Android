package com.network.network

import com.network.network.UrlManager.BASE_URL
import com.network.utils.AppClass
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    //retrofit http client
    private var retrofit: Retrofit? = null

    fun getInstance(): Retrofit? {
        //network interceptor
        val networkConnectionInterceptor = NetworkInterceptor(AppClass.instance)

        //body interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        //http client
        val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(networkConnectionInterceptor).addInterceptor(interceptor).build()

        //setting gson to lenient true
        val gson = GsonBuilder().setLenient().create()

        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
        }
        return retrofit
    }
}