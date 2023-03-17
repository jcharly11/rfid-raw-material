package com.checkpoint.rfid_raw_material.source.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class TagsLogs (
    val version: String,
    val subversion: String,
    val type: String,
    val piece: String,
    val provider: String,
    val epc: String,
    val timestamp: String
)