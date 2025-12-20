package com.example.crdemo.data.di

import DynamicBaseUrlInterceptor
import com.example.crdemo.data.network.BaseUrlProvider
import com.example.crdemo.data.network.ZooApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://data.taipei00/"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        baseUrlProvider: BaseUrlProvider
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(DynamicBaseUrlInterceptor(baseUrlProvider))
            .build()
    }


    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideZooApiService(retrofit: Retrofit): ZooApiService {
        return retrofit.create(ZooApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BaseUrlBindModule {

    @Binds
    @Singleton
    abstract fun bindBaseUrlProvider(
        impl: InMemoryBaseUrlProvider
    ): BaseUrlProvider
}