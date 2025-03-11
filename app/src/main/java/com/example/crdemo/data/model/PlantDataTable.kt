package com.example.crdemo.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantDataTable(
    @PrimaryKey val id: Int,
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
