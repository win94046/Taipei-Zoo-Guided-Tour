package com.example.crdemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crdemo.data.model.AnimalData
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.Exhibit
import com.example.crdemo.data.model.ExhibitDetailView
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantData
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.data.repository.ZooRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZooViewModel @Inject constructor(
    private val repository: ZooRepository
) : ViewModel() {

    // 🔥 LiveData - 監聽動物、植物、展覽數據
    val allAnimals: LiveData<List<AnimalDataTable>> = repository.getAllAnimals()
    val allPlants: LiveData<List<PlantDataTable>> = repository.getAllPlants()
    val exhibits: LiveData<List<ExhibitTable>> = repository.getAllExhibits()

    // 觀察特定展覽的動植物
    private val _exhibitDetail = MutableLiveData<ExhibitDetailView?>()
    val exhibitDetail: LiveData<ExhibitDetailView?> get() = _exhibitDetail

    fun refreshAllData() {
        viewModelScope.launch {
            repository.refreshAnimals()
            repository.refreshPlants()
            repository.refreshExhibits()
        }
    }

    fun getExhibitDetails(exhibitName: String) {
        Log.d("ZooViewModel", "getExhibitDetails called with exhibitName: $exhibitName")
        repository.getExhibitDetails(exhibitName).observeForever {
            _exhibitDetail.postValue(it)
        }
    }


    // 🔄 使用 ViewModelScope 進行協程操作
    fun refreshAnimals() {
        viewModelScope.launch {
            repository.refreshAnimals()
        }
    }

    fun refreshPlants() {
        viewModelScope.launch {
            repository.refreshPlants()
        }
    }

    fun refreshExhibits() {
        viewModelScope.launch {
            repository.refreshExhibits()
        }
    }

    // 🔍 根據 ID 查詢特定動物
    fun getAnimalById(id: Int): LiveData<AnimalDataTable> {
        return repository.getAnimalById(id)
    }

    // 🔍 根據 ID 查詢特定植物
    fun getPlantById(id: Int): LiveData<PlantDataTable> {
        return repository.getPlantById(id)
    }

    // 🔍 根據 ID 查詢特定展覽
    fun getExhibitById(id: Int): LiveData<ExhibitTable> {
        return repository.getExhibitById(id)
    }
}