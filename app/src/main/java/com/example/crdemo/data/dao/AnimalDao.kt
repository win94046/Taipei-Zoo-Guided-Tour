package com.example.crdemo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.crdemo.data.model.AnimalDataTable


@Dao
interface AnimalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<AnimalDataTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalDataTable)

    @Query("SELECT * FROM animals WHERE id = :id")
    fun getAnimalById(id: Int): LiveData<AnimalDataTable>

    @Query("SELECT * FROM animals")
    fun getAllAnimals(): LiveData<List<AnimalDataTable>>

    @Delete
    suspend fun deleteAnimal(animal: AnimalDataTable)
}
