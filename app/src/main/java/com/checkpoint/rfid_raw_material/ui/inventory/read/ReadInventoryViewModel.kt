package com.checkpoint.rfid_raw_material.ui.inventory.read

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.Tags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReadInventoryViewModel(application: Application) :AndroidViewModel(application){
    private var repository: DataRepository
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
    }

    suspend fun getTagsList(): List<Tags> = withContext(
        Dispatchers.IO) {
        repository.getTagsList()
    }


    suspend fun counterTags(): LiveData<List<Tags>> = withContext(
        Dispatchers.IO) {
        repository.getTagsListLive()
    }

    suspend fun getInventoryList(): LiveData<List<Inventory>> = withContext(
        Dispatchers.IO) {
        repository.getInventoryList()
    }


    fun pauseInventory(status: Boolean){
        localSharedPreferences.setPauseStatus(status)
    }


    suspend fun insertInventory(inventory: Inventory): Inventory = withContext(
        Dispatchers.IO) {
        repository.insertNewInventory(inventory)
    }

    fun disconnectDevice() {


    }

}
