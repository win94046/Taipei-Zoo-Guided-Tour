package com.example.crdemo.data.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.*
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.ExhibitDetailView
import com.example.crdemo.data.model.ExhibitTable

@Dao
interface ExhibitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExhibits(exhibits: List<ExhibitTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExhibit(exhibit: ExhibitTable)

    @Query("SELECT * FROM exhibits WHERE _id = :id")
    fun getExhibitById(id: Int): Flow<ExhibitTable>

    //  新增 suspend 版本，直接返回 List，適合一次性查詢
    @Query("SELECT * FROM exhibits")
    suspend fun getAllExhibitsList(): List<ExhibitTable>

    @Query("SELECT * FROM exhibits")
    fun getAllExhibits(): Flow<List<ExhibitTable>>

    @Delete
    suspend fun deleteExhibit(exhibit: ExhibitTable)


    @Query("SELECT * FROM ExhibitDetailView WHERE exhibitName = :exhibitName")
    fun getExhibitDetail(exhibitName: String): Flow<ExhibitDetailView?>
}
