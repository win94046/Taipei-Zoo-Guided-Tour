package com.example.crdemo.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://data.taipei/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 記錄請求與回應內容
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // 設定 OkHttp 客戶端
        .build()

    val api: ZooApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 設定 API 伺服器網址
            .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析 JSON
            .client(client)
            .build()
            .create(ZooApiService::class.java)
    }
}