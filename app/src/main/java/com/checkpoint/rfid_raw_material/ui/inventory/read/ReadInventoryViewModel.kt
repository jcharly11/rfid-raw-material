package com.checkpoint.rfid_raw_material.ui.inventory.read

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.ZebraRFIDHandlerImpl
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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

    suspend fun getInventoryList(): LiveData<List<Inventory>> = withContext(
        Dispatchers.IO) {
        repository.getInventoryList()
    }


    fun pauseInventory(status: Boolean){
        localSharedPreferences.setPauseStatus(status)
    }
}
