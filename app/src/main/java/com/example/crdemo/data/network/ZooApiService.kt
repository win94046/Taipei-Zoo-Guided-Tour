package com.example.crdemo.data.network

import com.example.crdemo.data.model.ZooResponse
import retrofit2.Response
import retrofit2.http.GET

interface ZooApiService {
    @GET("api/v1/dataset/9683ba26-109e-4cb8-8f3d-03d1b349db9f?scope=resourceAquire")
    suspend fun getZooData(): Response<ZooResponse>
}