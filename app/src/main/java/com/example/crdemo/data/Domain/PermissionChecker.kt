package com.example.crdemo.data.Domain

interface PermissionChecker {
    fun hasPermission(permission: String): Boolean
}