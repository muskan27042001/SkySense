package com.example.weather1.service

import com.example.weather1.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            // to log responses of retrofit
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            // Creating OkHTTP Client
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            // Retrofit Builder
            Retrofit.Builder().baseUrl(Utils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
        }
        // we will use this to make api calls
        val api by lazy {
            retrofit.create(API::class.java)
        }
    }
}