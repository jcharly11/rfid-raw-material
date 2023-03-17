package com.checkpoint.rfid_raw_material.ui.test

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.checkpoint.rfid_raw_material.source.db.tblItem
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TestViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: DataRepository

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
    }

    suspend fun getItemsList(): List<tblItem> {
        return repository.getItems()
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