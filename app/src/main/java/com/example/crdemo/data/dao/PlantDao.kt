package com.example.crdemo.data.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantDataTable

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlants(plants: List<PlantDataTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantDataTable)

    @Query("SELECT * FROM plants WHERE id = :id")
    fun getPlantById(id: Int): LiveData<PlantDataTable>

    @Query("SELECT * FROM plants")
    suspend fun getAllPlantsList(): List<PlantDataTable>

    @Query("SELECT * FROM plants")
    fun getAllPlants(): LiveData<List<PlantDataTable>>

    @Delete
    suspend fun deletePlant(plant: PlantDataTable)
}
