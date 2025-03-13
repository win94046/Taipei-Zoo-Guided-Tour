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
    @SerializedName("_id")
    val id: Int,

    @SerializedName("_importdate")
    val importDate: ImportDate?,

    @SerializedName("f_name_ch")
    val nameChinese: String,

    @SerializedName("f_name_en")
    val nameEnglish: String,

    @SerializedName("f_name_latin")
    val nameLatin: String,

    @SerializedName("f_family")
    val family: String,

    @SerializedName("f_genus")
    val genus: String,

    @SerializedName("f_brief")
    val brief: String,

    @SerializedName("f_feature")
    val feature: String,

    @SerializedName("f_function＆application")
    val functionAndApplication: String,

    @SerializedName("f_alsoknown")
    val alsoKnown: String?,

    @SerializedName("f_geo")
    val geoLocation: String?,

    @SerializedName("f_location")
    val location: String?,

    @SerializedName("f_code")
    val code: String?,

    @SerializedName("f_pic01_url")
    val imageUrl: String?,

    @SerializedName("f_update")
    val update: String?,

    @SerializedName("f_cid")
    val cid: String?
)

