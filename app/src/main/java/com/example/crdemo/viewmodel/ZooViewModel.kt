package com.example.crdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crdemo.data.model.Exhibit
import com.example.crdemo.data.model.ZooResponse
import com.example.crdemo.data.repository.ZooRepository
import kotlinx.coroutines.launch

class ZooViewModel : ViewModel() {
    private val repository = ZooRepository()
    private val _exhibits = MutableLiveData<List<Exhibit>>()
    val exhibits: LiveData<List<Exhibit>> get() = _exhibits

    fun loadZooData(jsonString: String) {
        val zooResponse = repository.parseJson(jsonString)
        _exhibits.value = zooResponse.result.results
    }

    fun fetchZooData() {
        viewModelScope.launch {
            try {
                val response = repository.getZooData()
                if (response.isSuccessful) {
                    _exhibits.value = response.body()?.result?.results ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}