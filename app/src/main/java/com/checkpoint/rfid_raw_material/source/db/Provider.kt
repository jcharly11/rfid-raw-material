package com.checkpoint.rfid_raw_material.source.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Provider")
class Provider(
    @PrimaryKey(autoGenerate = true) val idProvider: Int,
    @ColumnInfo(name = "id" ) val id: Int,
    @ColumnInfo(name = "idAS" ) val idAS: String,
    @ColumnInfo(name = "name" ) val name: String
)