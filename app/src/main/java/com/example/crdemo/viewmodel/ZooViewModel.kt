package com.example.crdemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
) : ViewModel()
{

    // 🔥 LiveData - 監聽動物、植物、展覽數據
    val allAnimals: LiveData<List<AnimalDataTable>> = repository.getAllAnimals()
    val allPlants: LiveData<List<PlantDataTable>> = repository.getAllPlants()
    val exhibits: LiveData<List<ExhibitTable>> = repository.getAllExhibits()

    private var currentObserver: Observer<List<Any>>? = null

    // 當前顯示的數據類型
    private val _currentList = MutableLiveData<List<Any>>()
    val currentList: LiveData<List<Any>> get() = _currentList

    // 當前顯示的適配器類型
    private val _currentAdapterType = MutableLiveData<ListType>()
    val currentAdapterType: LiveData<ListType> get() = _currentAdapterType

    init {
        setAdapterType(ListType.ANIMAL) // 預設顯示動物
    }



    // 觀察特定展覽的動植物
    private val _exhibitDetail = MutableLiveData<ExhibitDetailView?>()
    val exhibitDetail: LiveData<ExhibitDetailView?> get() = _exhibitDetail

    fun setAdapterType(type: ListType) {
        if (_currentAdapterType.value == type) return // 避免重複註冊

        _currentAdapterType.value = type
        _currentList.value = emptyList() // 清空當前列表，防止顯示錯誤 UI

        // 先移除舊的觀察者
        currentObserver?.let {
            when (_currentAdapterType.value) {
                ListType.ANIMAL -> allAnimals.removeObserver(it as Observer<List<AnimalDataTable>>)
                ListType.PLANT -> allPlants.removeObserver(it as Observer<List<PlantDataTable>>)
                ListType.EXHIBIT -> exhibits.removeObserver(it as Observer<List<ExhibitTable>>)
                else -> {}
            }
        }

        // 設定新的觀察者
        val observer = Observer<List<Any>> { list -> _currentList.postValue(list) }
        currentObserver = observer

        when (type) {
            ListType.ANIMAL -> allAnimals.observeForever(observer as Observer<List<AnimalDataTable>>)
            ListType.PLANT -> allPlants.observeForever(observer as Observer<List<PlantDataTable>>)
            ListType.EXHIBIT -> exhibits.observeForever(observer as Observer<List<ExhibitTable>>)
        }
    }

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

// 定義類型
enum class ListType {
    ANIMAL, PLANT, EXHIBIT
}