package com.checkpoint.rfid_raw_material.source

import com.checkpoint.rfid_raw_material.db.tblItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRepository(private val localDataSource: ItemsDatabase) {

    companion object {
        private var INSTANCE: DataRepository? = null
        fun getInstance(
            localDataSource: ItemsDatabase
        ): DataRepository {
            if (INSTANCE == null) {
                INSTANCE = DataRepository(localDataSource)
            }
            return INSTANCE!!
        }
    }

    suspend fun getItems():List<tblItem> = withContext(Dispatchers.IO) {
        localDataSource.itemDao().getItems()
    }

    suspend fun getItemsFilter(id:Int):List<tblItem> = withContext(Dispatchers.IO) {
        localDataSource.itemDao().getItemsFilter(id)
    }

    suspend fun insertNewItem(item: tblItem):tblItem = withContext(Dispatchers.IO) {
        localDataSource.itemDao().insertItem(item)
        localDataSource.itemDao().getLastItem()
    }
}