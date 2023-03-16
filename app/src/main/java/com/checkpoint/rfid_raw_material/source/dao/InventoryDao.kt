package com.checkpoint.rfid_raw_material.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.tblItem

@Dao
interface InventoryDao {
    @Query("SELECT * FROM Inventory")
    fun getInventoryList(): LiveData<List<Inventory>>

    @Query("SELECT * FROM Inventory")
    fun getInventoryListLogs(): List<Inventory>

    @Query("SELECT * FROM Inventory ORDER BY ID DESC LIMIT 1")
    fun getLastInventory(): Inventory


    @Insert
    fun  insertInventory(inventory: Inventory)
}