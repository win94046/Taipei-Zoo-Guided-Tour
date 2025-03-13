package com.example.crdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "animals")
data class AnimalDataTable(
    @PrimaryKey val id: Int,
    @SerializedName("a_name_ch") val nameChinese: String,
    @SerializedName("a_name_en") val nameEnglish: String,
    @SerializedName("a_alsoknown") val alsoKnown: String,
    @SerializedName("a_name_latin") val nameLatin: String,
    @SerializedName("a_phylum") val phylum: String,
    @SerializedName("a_class") val animalClass: String,
    @SerializedName("a_order") val order: String,
    @SerializedName("a_family") val family: String,
    @SerializedName("a_conservation") val conservation: String,
    @SerializedName("a_distribution") val distribution: String,
    val habitat: String?,
    val feature: String?,
    val behavior: String?,
    val diet: String?,
    val crisis: String?,
    val code: String?,
    val location: String,
    val pic01Url: String?,
    val pic02Url: String?,
    val pic03Url: String?,
    val pic04Url: String?,
    val videoUrl: String?,
    val update: String?,
    val cid: String?,
    val date: String,
    val timezone_type: Int,
    val timezone: String
)
