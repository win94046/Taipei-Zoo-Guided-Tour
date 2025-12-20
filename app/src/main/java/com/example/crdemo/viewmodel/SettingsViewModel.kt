package com.example.crdemo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.crdemo.data.network.BaseUrlProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val baseUrlProvider: BaseUrlProvider
) : ViewModel(){
    fun onBaseUrlChanged(newUrl: String) {
        baseUrlProvider.updateBaseUrl(newUrl)
    }
    fun changeTaipeiZooUrl(){
        baseUrlProvider.updateBaseUrl("https://data.taipei/")
    }
}