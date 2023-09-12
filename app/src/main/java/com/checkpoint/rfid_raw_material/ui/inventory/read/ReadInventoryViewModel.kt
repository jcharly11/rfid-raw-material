package com.checkpoint.rfid_raw_material.ui.inventory.read

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.source.model.TagsLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReadInventoryViewModel(application: Application) :AndroidViewModel(application){
    private var repository: DataRepository
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
        localSharedPreferences.setPauseStatus(true)

    }

    fun getTagsListLive(readNumber:Int): LiveData<List<Tags>>{
        val listData=repository.getTagsListLive(readNumber)
         return listData
    }

    suspend fun getTagsList(readNumber:Int): List<Tags> = withContext(
        Dispatchers.IO) {
        repository.getTagsList(readNumber)
    }


    fun pauseInventory(status: Boolean){
        localSharedPreferences.setPauseStatus(status)
    }



    suspend fun getNewReadNumber():Int= withContext(Dispatchers.IO){
        var readNumber= repository.getReadNumber()
        saveReadNumber(readNumber)
        readNumber
    }

    suspend fun getTagsForLog(readNumber: Int): List<TagsLogs> = withContext(Dispatchers.IO){
        repository.getTagsListForLogs(readNumber)
    }



    fun saveReadNumber(readNumber: Int){
        localSharedPreferences.saveReadNumber(readNumber)
    }

    fun getReadNumber():Int{
        return localSharedPreferences.getReadNumber()
    }

    suspend fun deleteCapturedData():Boolean = withContext(Dispatchers.IO){
        repository.deleteTagsInInventory()
        repository.countTags().equals(0)
    }

}
