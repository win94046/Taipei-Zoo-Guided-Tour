package com.example.crdemo.data

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.crdemo.data.Domain.PermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionCheckerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionChecker {

    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}