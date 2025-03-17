package com.example.crdemo.viewmodel

import android.util.Log
import androidx.lifecycle.*

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.crdemo.BuildConfig.apiKey
import com.example.crdemo.data.model.AnimalData
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.Exhibit
import com.example.crdemo.data.model.ExhibitDetailView
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantData
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.data.repository.ZooRepository
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ZooViewModel @Inject constructor(
    private val repository: ZooRepository
) : ViewModel()
{


    // 管理 選單點擊事件
    private val _closeDrawerEvent = MutableLiveData<Unit>()
    val closeDrawerEvent: LiveData<Unit> get() = _closeDrawerEvent

    //  LiveData - 監聽動物、植物、展覽數據
    val allAnimals: LiveData<List<AnimalDataTable>> = repository.getAllAnimals()
    val allPlants: LiveData<List<PlantDataTable>> = repository.getAllPlants()
    val exhibits: LiveData<List<ExhibitTable>> = repository.getAllExhibits()



    // 當前顯示的適配器類型
    private val _currentAdapterType = MutableLiveData<ListType>()
    val currentAdapterType: LiveData<ListType> get() = _currentAdapterType

    // 當前顯示的數據類型
    // 讓 currentList 根據 currentAdapterType 自動變更數據來源
    val currentList: LiveData<List<Any>> = _currentAdapterType.switchMap{ type ->
        when (type) {
            ListType.ANIMAL -> allAnimals as LiveData<List<Any>>
            ListType.PLANT -> allPlants as LiveData<List<Any>>
            ListType.EXHIBIT -> exhibits as LiveData<List<Any>>
            else -> MutableLiveData(emptyList())
        }
    }

    //
    private val _navigateToDetail = MutableLiveData<Event<Pair<ListType, Int>>>()
    val navigateToDetail: LiveData<Event<Pair<ListType, Int>>> get() = _navigateToDetail

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
    )

    init {
        setAdapterType(ListType.ANIMAL) // 預設顯示動物
    }

    val message: MutableLiveData<String> = MutableLiveData()



    fun setAdapterType(type: ListType) {
        if (_currentAdapterType.value != type) { // 避免重複設定相同類型
            _currentAdapterType.value = type
        }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            repository.refreshAnimals()
            repository.refreshPlants()
            repository.refreshExhibits()
        }
    }


    fun onMenuItemClicked(type: ListType) {
        setAdapterType(type)
        _closeDrawerEvent.value = Unit // 發送 UI 事件，通知 View 關閉 Drawer
    }

    fun onItemClicked(type: ListType, id: Int) {
        _navigateToDetail.value = Event(Pair(type, id)) // 確保事件只會觸發一次
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

open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /** 取得內容，確保只會被使用一次 */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /** 總是回傳內容，即使它已經被使用過 */
    fun peekContent(): T = content
}