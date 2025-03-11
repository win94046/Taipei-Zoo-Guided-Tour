package com.example.crdemo.data.di


import com.example.crdemo.data.dao.AnimalDao
import com.example.crdemo.data.dao.ExhibitDao
import com.example.crdemo.data.dao.PlantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDataRepository(
        animalDao: AnimalDao,
        plantDao: PlantDao,
        exhibitDao: ExhibitDao
    ): DataRepository {
        return DataRepository(animalDao, plantDao, exhibitDao)
    }

}
