package com.moveitech.dealerpay.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient  {


   private fun getRetrofit():Retrofit
    {
        val builder = OkHttpClient().newBuilder()
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.addInterceptor(getLoggingInterceptor())

        return Retrofit.Builder()
            .client(builder.build())
            .baseUrl("https://hotelapi.sbstorefsd.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApi(): Api
    {
        return  getRetrofit().create(Api::class.java)
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }
}