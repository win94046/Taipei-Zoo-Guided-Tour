package com.example.crdemo.data.network

interface BaseUrlProvider {
    fun getBaseUrl(): String
    fun updateBaseUrl(newBaseUrl: String)
}