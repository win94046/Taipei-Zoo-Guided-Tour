package com.example.crdemo.data.di

import com.example.crdemo.data.Domain.PermissionChecker
import com.example.crdemo.data.PermissionCheckerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {

    @Binds
    @Singleton
    abstract fun bindPermissionChecker(
        permissionCheckerImpl: PermissionCheckerImpl
    ): PermissionChecker
}