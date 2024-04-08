package com.network.network

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

    fun getInstance(baseUrl: String): Retrofit? {
        //network interceptor
        val networkConnectionInterceptor = NetworkInterceptor(AppClass.instance)

        //body interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        //http client
        val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(networkConnectionInterceptor).addInterceptor(interceptor).build()

        //setting gson to lenient true
        val gson = GsonBuilder().setLenient().create()

        if (retrofit == null || baseUrl != retrofit?.baseUrl().toString()) {
            retrofit = Retrofit.Builder().baseUrl(baseUrl).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
        }
        return retrofit
    }
}