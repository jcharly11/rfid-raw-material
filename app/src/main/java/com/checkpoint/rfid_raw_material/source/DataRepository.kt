package com.checkpoint.rfid_raw_material.source

import com.checkpoint.rfid_raw_material.source.dao.ProviderDao
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.tblItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRepository(private val localDataSource: RawMaterialsDatabase) {

    companion object {
        private var INSTANCE: DataRepository? = null
        fun getInstance(
            localDataSource: RawMaterialsDatabase
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


    suspend fun getProviders():List<Provider> = withContext(Dispatchers.IO) {
        localDataSource.providerDao().getProviders()
    }

    suspend fun insertNewProvider(provider: Provider):Provider = withContext(Dispatchers.IO) {
        localDataSource.providerDao().insertProvider(provider)
        localDataSource.providerDao().getLastProvider()
    }
}