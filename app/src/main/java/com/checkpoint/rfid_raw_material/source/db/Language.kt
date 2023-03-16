package com.checkpoint.rfid_raw_material.source.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Language")
data class Language (
    @PrimaryKey(autoGenerate = true) val idLanguage: Int,
    @ColumnInfo(name = "language" ) val language: String,
    @ColumnInfo(name = "lang" ) val lang: String
)