package com.example.crdemo.data.repository


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.crdemo.data.dao.AnimalDao
import com.example.crdemo.data.dao.ExhibitDao
import com.example.crdemo.data.dao.PlantDao
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.AnimalResponse
import com.example.crdemo.data.model.ExhibitDetailView
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.data.model.PlantsResponse
import com.example.crdemo.data.model.ZooResponse
import com.example.crdemo.data.network.RetrofitInstance
import com.example.crdemo.data.network.ZooApiService
import com.google.gson.Gson
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ZooRepository @Inject constructor(
    private val animalDao: AnimalDao,
    private val plantDao: PlantDao,
    private val exhibitDao: ExhibitDao,
    private val apiService: ZooApiService
) {
    private val gson = Gson()

    /**
     * 解析 JSON
     */
    fun parseJson(jsonString: String): ZooResponse {
        return gson.fromJson(jsonString, ZooResponse::class.java)
    }

    // 🔍 查詢特定動物
    fun getAnimalById(id: Int): LiveData<AnimalDataTable> {
        return animalDao.getAnimalById(id)
    }

    // 🔍 查詢特定植物
    fun getPlantById(id: Int): LiveData<PlantDataTable> {
        return plantDao.getPlantById(id)
    }

    // 🔍 查詢特定展覽
    fun getExhibitById(id: Int): LiveData<ExhibitTable> {
        return exhibitDao.getExhibitById(id)
    }

    /**
     * 🔥 獲取動物資料 (先從 Room 讀取，如果沒有則從 API 請求並存入本地)
     */
    fun getAllAnimals(): LiveData<List<AnimalDataTable>> {
        return animalDao.getAllAnimals()
    }

    suspend fun refreshAnimals() {
        try {
            val response = apiService.getAnimalData()
            if (response.isSuccessful) {
                response.body()?.let { animalResponse ->
                    // 轉換 API 數據為 Room 實體
                    val animalList = animalResponse.result.animalData.map {
                        AnimalDataTable(
                            id = it.id,
                            nameChinese = it.nameChinese,
                            nameEnglish = it.nameEnglish,
                            alsoKnown = it.alsoKnown,
                            nameLatin = it.nameLatin,
                            phylum = it.phylum,
                            animalClass = it.animalClass,
                            order = it.order,
                            family = it.family,
                            conservation = it.conservation,
                            distribution = it.distribution,
                            habitat = it.habitat,
                            feature = it.feature,
                            behavior = it.behavior,
                            diet = it.diet,
                            crisis = it.crisis,
                            code = it.code,
                            location = it.location,
                            pic01Url = it.pic01Url,
                            pic02Url = it.pic02Url,
                            pic03Url = it.pic03Url,
                            pic04Url = it.pic04Url,
                            videoUrl = it.videoUrl,
                            update = it.update,
                            cid = it.cid,
                            date = it.importDate.date,
                            timezone_type = it.importDate.timezone_type,
                            timezone = it.importDate.timezone
                        )
                    }
                    // 存入 Room
                    animalDao.insertAnimals(animalList)
                }
            }
        } catch (e: Exception) {
            Log.e("ZooRepository", "Error fetching animal data", e)
        }
    }

    /**
     * 🔥 獲取植物資料 (同上)
     */
    fun getAllPlants(): LiveData<List<PlantDataTable>> {
        return plantDao.getAllPlants()
    }

    suspend fun refreshPlants() {
        try {
            val response = apiService.getPlantData()
            if (response.isSuccessful) {
                response.body()?.let { plantResponse ->
                    val plantList = plantResponse.result.results.map {
                        PlantDataTable(
                            id = it.id,
                            date = it.importDate?.date ?: "", // ✅ 避免 NullPointerException
                            timezone_type = it.importDate?.timezone_type ?: 0,
                            timezone = it.importDate?.timezone ?: "",
                            nameChinese = it.nameChinese,
                            nameEnglish = it.nameEnglish,
                            nameLatin = it.nameLatin,
                            family = it.family,
                            genus = it.genus,
                            brief = it.brief,
                            feature = it.feature,
                            functionAndApplication = it.functionAndApplication,
                            alsoKnown = it.alsoKnown,
                            geoLocation = it.geoLocation,
                            location = it.location,
                            code = it.code,
                            imageUrl = it.imageUrl,
                            update = it.update,
                            cid = it.cid
                        )
                    }
                    plantDao.insertPlants(plantList)
                }
            }
        } catch (e: Exception) {
            Log.e("ZooRepository", "Error fetching plant data", e)
        }
    }


    /**
     * 🔥 獲取展覽資料 (同上)
     */
    fun getAllExhibits(): LiveData<List<ExhibitTable>> {
        return exhibitDao.getAllExhibits()
    }

    suspend fun refreshExhibits() {
        try {
            val response = apiService.getZooData()
            if (response.isSuccessful) {
                response.body()?.let { exhibitResponse ->
                    val exhibitList = exhibitResponse.result.results.map {
                        ExhibitTable(
                            _id = it._id,
                            e_no = it.e_no,
                            e_category = it.e_category,
                            e_name = it.e_name,
                            e_pic_url = it.e_pic_url,
                            e_info = it.e_info,
                            e_memo = it.e_memo,
                            e_geo = it.e_geo,
                            e_url = it.e_url,
                            date = it._importdate.date,
                            timezone_type = it._importdate.timezone_type,
                            timezone = it._importdate.timezone
                        )
                    }
                    exhibitDao.insertExhibits(exhibitList)
                }
            }
        } catch (e: Exception) {
            Log.e("ZooRepository", "Error fetching exhibit data", e)
        }
    }


    fun getExhibitDetails(exhibitName: String): LiveData<ExhibitDetailView?> {
        return exhibitDao.getExhibitDetail(exhibitName)
    }

}
