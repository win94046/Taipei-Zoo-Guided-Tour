package com.example.crdemo.data.model

import com.google.gson.annotations.SerializedName

// 頂層 JSON 物件
data class AnimalResponse(
    @SerializedName("result") val result: ZooResult
)

// 包含動物列表的結果
data class ZooResult(
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("sort") val sort: String,
    @SerializedName("results") val animals: List<Animal>
)

// 個別動物的詳細資料
data class Animal(
    @SerializedName("_id") val id: Int,
    @SerializedName("_importdate") val importDate: ImportDate,
    @SerializedName("a_name_ch") val nameChinese: String,
    @SerializedName("a_name_en") val nameEnglish: String,
    @SerializedName("a_name_latin") val nameLatin: String,
    @SerializedName("a_phylum") val phylum: String,
    @SerializedName("a_class") val animalClass: String,
    @SerializedName("a_order") val order: String,
    @SerializedName("a_family") val family: String,
    @SerializedName("a_conservation") val conservation: String,
    @SerializedName("a_distribution") val distribution: String,
    @SerializedName("a_habitat") val habitat: String?,
    @SerializedName("a_feature") val feature: String?,
    @SerializedName("a_behavior") val behavior: String?,
    @SerializedName("a_diet") val diet: String?,
    @SerializedName("a_crisis") val crisis: String?,
    @SerializedName("a_code") val code: String?,
    @SerializedName("a_location") val location: String,
    @SerializedName("a_pic01_url") val pic01Url: String?,
    @SerializedName("a_pic02_url") val pic02Url: String?,
    @SerializedName("a_pic03_url") val pic03Url: String?,
    @SerializedName("a_pic04_url") val pic04Url: String?,
    @SerializedName("a_vedio_url") val videoUrl: String?,
    @SerializedName("a_update") val update: String?,
    @SerializedName("a_cid") val cid: String?
)
