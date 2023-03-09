package com.checkpoint.rfid_raw_material.ui.inventory.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.Tags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemsReadViewModel(application: Application) : AndroidViewModel(application)
{
    private var repository: DataRepository
    private val _listItems: MutableLiveData<List<Inventory>> = MutableLiveData()
    var listItems: LiveData<List<Inventory>> = _listItems

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
    }

    suspend fun getInventoryList(): LiveData<List<Inventory>> =withContext(
    Dispatchers.IO) {
        listItems=repository.getInventoryList()
        listItems
    }

    suspend fun getTagsList(): List<Tags> =withContext(
        Dispatchers.IO) {
        repository.getTagsList()
    }



}