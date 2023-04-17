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


    suspend fun getTagsList(readNumber: Int): List<Tags> = withContext(Dispatchers.IO) {
        localDataSource.tagsDao().getTagsList(readNumber)
    }

    fun getTagsListLive(readNumber: Int):LiveData<List<Tags>> {
        return localDataSource.tagsDao().getTagsListLive(readNumber)
    }

    suspend fun getTagsListForLogs(readNumber:Int): List<TagsLogs> = withContext(Dispatchers.IO) {
        localDataSource.tagsDao().getTagsListForLogs(readNumber)
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


    fun deletePoviders() {
        localDataSource.providerDao().deleteAll()
    }

    suspend fun getReadNumber():Int= withContext(Dispatchers.IO) {
        var list= localDataSource.tagsDao().getReadNumber()
        if(list.size>0)
            list[0].readNumber+1
        else
            1
    }

    fun deleteTagsInInventory() {
        localDataSource.tagsDao().deleteAllTags()
    }

    suspend fun countTags(): Int= withContext(Dispatchers.IO){
        localDataSource.tagsDao().getTagsList().size
    }

    fun deleteProvider(idProvider:Int) {
        localDataSource.providerDao().deleteProvider(idProvider)
    }

}