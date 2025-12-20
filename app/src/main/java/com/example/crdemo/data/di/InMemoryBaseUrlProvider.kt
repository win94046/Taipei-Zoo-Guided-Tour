package com.example.crdemo.data.di

import com.example.crdemo.data.network.BaseUrlProvider
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryBaseUrlProvider @Inject constructor() : BaseUrlProvider {
    private val ref = AtomicReference("https://api.example.com/") // 預設值

    override fun getBaseUrl(): String = ref.get()

    override fun updateBaseUrl(newBaseUrl: String) {
        ref.set(newBaseUrl)
    }
}