package com.example.crdemo.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantDataTable(
    @PrimaryKey val id: Int,
    val date: String?,
    val timezone_type: Int,
    val timezone: String,
    val nameChinese: String,
    val nameEnglish: String,
    val nameLatin: String,
    val family: String,
    val genus: String,
    val brief: String,
    val feature: String,
    val functionAndApplication: String,
    val alsoKnown: String?,
    val geoLocation: String?,
    val location: String?,
    val code: String?,
    val imageUrl: String?,
    val update: String?,
    val cid: String?
)
