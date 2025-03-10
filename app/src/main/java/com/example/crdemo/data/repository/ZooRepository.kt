package com.example.crdemo.data.repository


import com.example.crdemo.data.model.ZooResponse
import com.example.crdemo.data.network.RetrofitInstance
import com.google.gson.Gson
import retrofit2.Response


class ZooRepository {
    fun parseJson(jsonString: String): ZooResponse {
        val gson = Gson()
        return gson.fromJson(jsonString, ZooResponse::class.java)
    }

    suspend fun getZooData(): Response<ZooResponse> {
        return RetrofitInstance.api.getZooData()
    }

}