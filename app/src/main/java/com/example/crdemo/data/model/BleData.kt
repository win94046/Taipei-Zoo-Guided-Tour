package com.example.crdemo.data.model

import java.util.UUID

// 檔案位置: model/BleData.kt 或 放在 BleRepository.kt 內部

data class BleData(
    val uuid: UUID,      // 特徵值的 ID (用來區分是按鈕數據、還是 LED 數據...)
    val value: ByteArray // 實際的數據內容 (例如: 0x01, 0x00)
) {
    // 覆寫 equals 和 hashCode 是因為 ByteArray 在 data class 比較時需要特殊處理
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BleData
        if (uuid != other.uuid) return false
        if (!value.contentEquals(other.value)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}