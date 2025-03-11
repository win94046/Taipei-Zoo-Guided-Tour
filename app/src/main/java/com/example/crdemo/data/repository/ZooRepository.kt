package com.example.crdemo.data.repository


import com.example.crdemo.data.dao.AnimalDao
import com.example.crdemo.data.dao.ExhibitDao
import com.example.crdemo.data.dao.PlantDao
import com.example.crdemo.data.model.AnimalResponse
import com.example.crdemo.data.model.PlantsResponse
import com.example.crdemo.data.model.ZooResponse
import com.example.crdemo.data.network.RetrofitInstance
import com.google.gson.Gson
import retrofit2.Response


class ZooRepository(
    private val animalDao: AnimalDao,
    private val plantDao: PlantDao,
    private val exhibitDao: ExhibitDao
) {
    fun parseJson(jsonString: String): ZooResponse {
        val gson = Gson()
        return gson.fromJson(jsonString, ZooResponse::class.java)
    }

    suspend fun getZooData(): Response<ZooResponse> {
        return RetrofitInstance.api.getZooData()
    }

    suspend fun getPlantData(): Response<PlantsResponse> {
        return RetrofitInstance.api.getPlantData()
    }

    suspend fun getAnimalData(): Response<AnimalResponse> {
        return RetrofitInstance.api.getAnimalData()

    }
}