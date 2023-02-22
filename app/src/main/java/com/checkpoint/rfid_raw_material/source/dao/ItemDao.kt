package com.checkpoint.rfid_raw_material.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.checkpoint.rfid_raw_material.db.tblItem

@Dao
interface ItemDao {

    @Query("SELECT * FROM tblItem")
    fun getItems(): List<tblItem>

    @Query("SELECT * FROM tblItem WHERE id=:idItem")
    fun getItemsFilter(idItem:Int): List<tblItem>

    @Query("SELECT * FROM tblItem ORDER BY ID DESC LIMIT 1")
    fun getLastItem(): tblItem


    @Insert
    fun insertItem(item: tblItem)
}