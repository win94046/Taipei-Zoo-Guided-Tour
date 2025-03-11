package com.example.crdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crdemo.data.model.AnimalData
import com.example.crdemo.data.model.Exhibit
import com.example.crdemo.data.model.PlantData
import com.example.crdemo.data.repository.ZooRepository
import kotlinx.coroutines.launch

class ZooViewModel : ViewModel() {
    private val repository = ZooRepository()
    private val _exhibits = MutableLiveData<List<Exhibit>>()
    val exhibits: LiveData<List<Exhibit>> get() = _exhibits

    private val _plantData = MutableLiveData<PlantData>()
    val plantData: LiveData<PlantData> get() = _plantData

    private val _animalDataData = MutableLiveData<List<AnimalData>>()
    val animalData: LiveData<List<AnimalData>> get() = _animalDataData

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

    fun fetchPlantData() {
        viewModelScope.launch {
            try {
                val response = repository.getPlantData()
                if (response.isSuccessful) {
                    _plantData.value = response.body()?.result?.results?.firstOrNull()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchAnimalData(){
        viewModelScope.launch {
            try {
                val response = repository.getAnimalData()
                if (response.isSuccessful) {
                    _animalDataData.value = response.body()?.result?.animalData ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}