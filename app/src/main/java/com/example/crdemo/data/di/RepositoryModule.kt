package com.example.crdemo.data.di


import android.content.Context
import com.example.crdemo.data.dao.AnimalDao
import com.example.crdemo.data.dao.ExhibitDao
import com.example.crdemo.data.dao.PlantDao
import com.example.crdemo.data.network.ZooApiService
import com.example.crdemo.data.repository.BleRepository
import com.example.crdemo.data.repository.ZooRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideZooRepository(
        animalDao: AnimalDao,
        plantDao: PlantDao,
        exhibitDao: ExhibitDao,
        apiService: ZooApiService
    ): ZooRepository {
        return ZooRepository(animalDao, plantDao, exhibitDao , apiService)
    }

//    @Singleton
//    @Provides
//    fun provideBleRepository(
//        @ApplicationContext context: Context
//    ): BleRepository {
//        return BleRepository(context)
//    }

}

