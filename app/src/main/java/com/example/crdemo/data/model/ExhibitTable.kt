package com.example.crdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded

@Entity(tableName = "exhibits")
data class ExhibitTable(
    @PrimaryKey val _id: Int,
    val e_no: String,
    val e_category: String,
    val e_name: String,
    val e_pic_url: String,
    val e_info: String,
    val e_memo: String,
    val e_geo: String,
    val e_url: String,
    val date: String,
    val timezone_type: Int,
    val timezone: String
)
