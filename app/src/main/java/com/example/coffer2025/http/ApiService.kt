package com.example.coffer2025.http

import com.example.coffer2025.model.ProjectListResponse
import retrofit2.http.GET

interface ApiService {

    @GET("/article/listproject/0/json")
    suspend fun getInfo(): ProjectListResponse
}