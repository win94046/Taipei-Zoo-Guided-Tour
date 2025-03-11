package com.example.crdemo.data.di



import android.content.Context
import androidx.room.Room
import com.example.crdemo.data.dao.AnimalDao
import com.example.crdemo.data.dao.ExhibitDao
import com.example.crdemo.data.dao.PlantDao
import com.example.crdemo.data.database.AppDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "zoo_database"
        ).build()
    }

    @Provides
    fun provideAnimalDao(database: AppDatabase): AnimalDao = database.animalDao()

    @Provides
    fun providePlantDao(database: AppDatabase): PlantDao = database.plantDao()

    @Provides
    fun provideExhibitDao(database: AppDatabase): ExhibitDao = database.exhibitDao()
}
