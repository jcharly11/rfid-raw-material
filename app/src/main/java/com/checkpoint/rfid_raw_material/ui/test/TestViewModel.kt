package com.checkpoint.rfid_raw_material.ui.test

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.db.tblItem
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.ItemsDatabase
import com.checkpoint.rfid_raw_material.source.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TestViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: DataRepository

    init {
        repository = DataRepository.getInstance(
            ItemsDatabase.getDatabase(application.baseContext)
        )
    }

    suspend fun getItemsList(): List<tblItem> {
        return repository.getItems()
    }

    suspend fun getItemsListForCSV(id:Int): List<tblItem> {
        return repository.getItemsFilter(id)
    }

    suspend fun newItem(itemName: String):
            tblItem = withContext(Dispatchers.IO) {
        repository.insertNewItem(
            tblItem(
                0,
                itemName
            )
        )
    }

}