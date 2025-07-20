package com.example.coffer2025.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}