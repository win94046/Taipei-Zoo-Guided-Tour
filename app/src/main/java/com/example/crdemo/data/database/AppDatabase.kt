package com.example.crdemo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.crdemo.data.dao.AnimalDao
import com.example.crdemo.data.dao.ExhibitDao
import com.example.crdemo.data.dao.PlantDao
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.ExhibitDetailView
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantDataTable


@Database(entities = [AnimalDataTable::class, PlantDataTable::class, ExhibitTable::class],
    views = [ExhibitDetailView::class],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun plantDao(): PlantDao
    abstract fun exhibitDao(): ExhibitDao

}
