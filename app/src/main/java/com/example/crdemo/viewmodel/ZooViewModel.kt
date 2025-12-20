package com.example.crdemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crdemo.BuildConfig.apiKey
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.data.repository.ZooRepository
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ZooViewModel @Inject constructor(
    private val repository: ZooRepository
) : ViewModel()
{


    // 管理 選單點擊事件
    private val _closeDrawerEvent = Channel<Unit>(Channel.BUFFERED)
    val closeDrawerEvent: Flow<Unit> = _closeDrawerEvent.receiveAsFlow()

    //  Flow - 監聽動物、植物、展覽數據
    val allAnimals: StateFlow<List<AnimalDataTable>> = repository.getAllAnimals()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allPlants: StateFlow<List<PlantDataTable>> = repository.getAllPlants()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val exhibits: StateFlow<List<ExhibitTable>> = repository.getAllExhibits()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    // 當前顯示的適配器類型
    private val _currentAdapterType = MutableStateFlow(ListType.ANIMAL)
    val currentAdapterType: StateFlow<ListType> = _currentAdapterType.asStateFlow()

    // 當前顯示的數據類型
    // 讓 currentList 根據 currentAdapterType 自動變更數據來源
    @Suppress("UNCHECKED_CAST")
    val currentList: StateFlow<List<Any>> = _currentAdapterType.flatMapLatest { type ->
        when (type) {
            ListType.ANIMAL -> allAnimals.map { it as List<Any> }
            ListType.PLANT -> allPlants.map { it as List<Any> }
            ListType.EXHIBIT -> exhibits.map { it as List<Any> }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    //
    private val _navigateToDetail = Channel<Pair<ListType, Int>>(Channel.BUFFERED)
    val navigateToDetail: Flow<Pair<ListType, Int>> = _navigateToDetail.receiveAsFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
    )

    init {
        // init is empty now as StateFlow needs initial value in constructor
    }

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()


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
        viewModelScope.launch {
            _closeDrawerEvent.send(Unit) // 發送 UI 事件，通知 View 關閉 Drawer
        }
    }

    fun onItemClicked(type: ListType, id: Int) {
        viewModelScope.launch {
            _navigateToDetail.send(Pair(type, id))
        }
    }

    // 🔍 根據 ID 查詢特定動物
    fun getAnimalById(id: Int): Flow<AnimalDataTable> {
        return repository.getAnimalById(id)
    }

    // 🔍 根據 ID 查詢特定植物
    fun getPlantById(id: Int): Flow<PlantDataTable> {
        return repository.getPlantById(id)
    }

    // 🔍 根據 ID 查詢特定展覽
    fun getExhibitById(id: Int): Flow<ExhibitTable> {
        return repository.getExhibitById(id)
    }

    fun updateMessage(text: String) {
        _message.value = text
        Log.d("ZooViewModel", "updateMessage called with text: $text")
    }


    suspend fun getAllDataString(): String {
        val animals = repository.getAllAnimalsList()
        val plants = repository.getAllPlantsList()
        val exhibits = repository.getAllExhibitsList()

        val animalsData = animals.joinToString("\n") { "動物: ${it.nameChinese} (學名: ${it.nameLatin})" }
        val plantsData = plants.joinToString("\n") { "植物: ${it.nameChinese} (學名: ${it.nameLatin})" }
        val exhibitsData = exhibits.joinToString("\n") { "展覽區: ${it.e_name} (類別: ${it.e_category})" }

        return """
        這是動物園的所有數據：
        🐾 動物：
        $animalsData

        🌱 植物：
        $plantsData

        🏛 展覽區：
        $exhibitsData
    """.trimIndent()
    }

    fun postMessage(message: String) {
        viewModelScope.launch {
            try {
                val databaseInfo = withContext(Dispatchers.IO) { getAllDataString() } // 🔥 確保 Room 操作在 IO 執行
                val prompt = """
                以下是動物園的所有資訊：
                $databaseInfo
                
                使用者問題：
                $message
                
                上述問題請用英文回答
            """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: "無回應"

                updateMessage(responseText)
            } catch (e: Exception) {
                Log.e("ZooViewModel", "Gemini API 錯誤: ${e.message}")
                updateMessage("請求失敗，請稍後再試")
            }
        }
    }

}

// 定義類型
enum class ListType {
    ANIMAL, PLANT, EXHIBIT
}
