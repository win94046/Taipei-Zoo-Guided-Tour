package com.example.crdemo.data.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.*
import com.example.crdemo.data.model.AnimalDataTable


@Dao
interface AnimalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<AnimalDataTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalDataTable)

    @Query("SELECT * FROM animals WHERE id = :id")
    fun getAnimalById(id: Int): Flow<AnimalDataTable>

    //  新增 suspend 版本，直接返回 List，適合一次性查詢
    @Query("SELECT * FROM animals")
    suspend fun getAllAnimalsList(): List<AnimalDataTable>

    @Query("SELECT * FROM animals")
    fun getAllAnimals(): Flow<List<AnimalDataTable>>

    @Delete
    suspend fun deleteAnimal(animal: AnimalDataTable)
}
