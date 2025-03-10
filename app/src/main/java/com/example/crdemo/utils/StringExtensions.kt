package com.example.crdemo.utils

fun String.toSecureUrl(): String {
    return this.replace("http://", "https://")
}
