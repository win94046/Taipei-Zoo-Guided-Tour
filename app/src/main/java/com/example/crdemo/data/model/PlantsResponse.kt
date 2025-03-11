package com.example.crdemo.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

// 外層 Result 物件
data class PlantsResponse(
    val result: ResultData
)

// 分頁資訊
data class ResultData(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val sort: String,
    val results: List<PlantData>
)

// 植物資料
data class PlantData(
    val id: Int,
    val nameChinese: String,
    val nameEnglish: String,
    val nameLatin: String,
    val family: String,
    val genus: String,
    val brief: String,
    val feature: String,
    val functionApplication: String,
    val imageUrl: String?
)
