package com.checkpoint.rfid_raw_material.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tblItem")
class tblItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "nameItem" ) val nameItem: String
)