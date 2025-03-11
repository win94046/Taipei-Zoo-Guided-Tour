package com.example.crdemo.data.network

import com.example.crdemo.data.model.AnimalResponse
import com.example.crdemo.data.model.PlantsResponse
import com.example.crdemo.data.model.ZooResponse
import retrofit2.Response
import retrofit2.http.GET

interface ZooApiService {
    @GET("api/v1/dataset/9683ba26-109e-4cb8-8f3d-03d1b349db9f?scope=resourceAquire")
    suspend fun getZooData(): Response<ZooResponse>

    @GET("api/v1/dataset/e20706d8-bf89-4e6a-9768-db2a10bb2ba4?scope=resourceAquire") // 請替換為實際 API 端點
    suspend fun getPlantData(): Response<PlantsResponse>

    @GET("api/v1/dataset/6afa114d-38a2-4e3c-9cfd-29d3bd26b65b?scope=resourceAquire")
    suspend fun getAnimalData(): Response<AnimalResponse>

}