package com.checkpoint.rfid_raw_material.source.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tags")
data class Tags (
@PrimaryKey(autoGenerate = true) val id: Int,
@ColumnInfo(name = "version") val version: String,
@ColumnInfo(name = "subversion") val subversion: String,
@ColumnInfo(name = "type") val type: String,
@ColumnInfo(name = "piece") val piece: String,
@ColumnInfo(name = "idProvider") val idProvider: Int,
@ColumnInfo(name = "epc") val epc: String,
@ColumnInfo(name = "timestamp") val timeStamp: String
)
