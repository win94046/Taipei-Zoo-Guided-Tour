package com.example.crdemo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.crdemo.data.model.ExhibitTable

@Dao
interface ExhibitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExhibit(exhibit: ExhibitTable)

    @Query("SELECT * FROM exhibits WHERE _id = :id")
    fun getExhibitById(id: Int): LiveData<ExhibitTable>

    @Query("SELECT * FROM exhibits")
    fun getAllExhibits(): LiveData<List<ExhibitTable>>

    @Delete
    suspend fun deleteExhibit(exhibit: ExhibitTable)
}
