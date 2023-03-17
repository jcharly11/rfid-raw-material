package com.checkpoint.rfid_raw_material.source

import androidx.lifecycle.LiveData
import com.checkpoint.rfid_raw_material.source.dao.ProviderDao
import com.checkpoint.rfid_raw_material.source.db.*
import com.checkpoint.rfid_raw_material.source.model.TagsLogs
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

    fun getInventoryList(): LiveData<List<Inventory>> {
        return localDataSource.inventoryDao().getInventoryList()
    }

    fun getInventoryListLogs():List<Inventory> {
        return localDataSource.inventoryDao().getInventoryListLogs()
    }

    suspend fun getTagsList(): List<Tags> = withContext(Dispatchers.IO) {
        localDataSource.tagsDao().getTagsList()
    }

    suspend fun getTagsListForLogs(): List<TagsLogs> = withContext(Dispatchers.IO) {
        localDataSource.tagsDao().getTagsListForLogs()
    }

    suspend fun insertNewTag(tag: Tags):Tags = withContext(Dispatchers.IO) {
        localDataSource.tagsDao().insertTag(tag)
        localDataSource.tagsDao().getLastTag()
    }


    suspend fun getLanguages():List<Language> = withContext(Dispatchers.IO) {
        localDataSource.languageDao().getLanguageList()
    }

    suspend fun insertNewLang(language: Language):Language = withContext(Dispatchers.IO) {
        localDataSource.languageDao().insertLanguage(language)
        localDataSource.languageDao().getLastLang()
    }

    suspend fun insertNewInventory(inventory: Inventory):Inventory = withContext(Dispatchers.IO) {
        localDataSource.inventoryDao().insertInventory(inventory)
        localDataSource.inventoryDao().getLastInventory()
    }
}