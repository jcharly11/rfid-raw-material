package com.checkpoint.rfid_raw_material.source.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Inventory")
data class Inventory(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "epc") val epc: String,
    @ColumnInfo(name = "timestamp") val timeStamp: String
)