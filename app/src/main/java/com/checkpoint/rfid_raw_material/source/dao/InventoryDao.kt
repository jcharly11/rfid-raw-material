package com.checkpoint.rfid_raw_material.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.checkpoint.rfid_raw_material.source.db.Inventory

@Dao
interface InventoryDao {
    @Query("SELECT * FROM Inventory")
    fun getInventoryList(): LiveData<List<Inventory>>


    @Insert
    fun  insertInventory(inventoryRead: Inventory)
}