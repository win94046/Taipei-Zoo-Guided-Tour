package com.example.crdemo.data.model

data class ZooResponse(
    val result: Result
)

data class Result(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val sort: String,
    val results: List<Exhibit>
)

data class Exhibit(
    val _id: Int,
    val _importdate: ImportDate,
    val e_no: String,
    val e_category: String,
    val e_name: String,
    val e_pic_url: String,
    val e_info: String,
    val e_memo: String,
    val e_geo: String,
    val e_url: String
)

data class ImportDate(
    val date: String,
    val timezone_type: Int,
    val timezone: String
)
