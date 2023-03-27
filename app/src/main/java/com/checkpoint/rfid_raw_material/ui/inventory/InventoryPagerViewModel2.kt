package com.checkpoint.rfid_raw_material.ui.inventory

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.model.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.RFIDHandler
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("MissingPermission")
class InventoryPagerViewModel2(application: Application) : AndroidViewModel(application),
    ResponseHandlerInterface,
    BatteryHandlerInterface {
    //private var repository: DataRepository
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)

    private val _dialogVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val dialogVisible: LiveData<Boolean> = _dialogVisible

    private val _percentCharge: MutableLiveData<Int> = MutableLiveData(0)
    val percentCharge: LiveData<Int> = _percentCharge


    private var bluetoothHandler: BluetoothHandler? = null
    private var rfidHandler: RFIDHandler?= null
    private var deviceName: String? = null
    private var context: Context = application.baseContext
    private var maxLevel = 0
    private var repository: DataRepository


    init {


        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
        maxLevel = localSharedPreferences.getMaxFromPreferences()
        Log.e("maxLevel--->", "$maxLevel")
        bluetoothHandler = BluetoothHandler(context)
        val devices = bluetoothHandler!!.list()

        if (devices != null) {
            for (device in devices) {
                if (device.name.contains("RFD8")) {
                    deviceName = device.name
                }
            }
        }

        rfidHandler = RFIDHandler(
            context,
            DeviceConfig(
                150,
                SESSION.SESSION_S1,
                deviceName!!,
                ENUM_TRIGGER_MODE.RFID_MODE,
                ENUM_TRANSPORT.BLUETOOTH
            )
        )
        rfidHandler!!.setResponseHandlerInterface(this)
        rfidHandler!!.setBatteryHandlerInterface(this)
        rfidHandler!!.batteryLevel()

        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )

    }


    override  fun handleTagdata(tagData: Array<TagData?>?) {
        val code = tagData?.get(0)?.tagID.toString()
        val rssi = tagData?.get(0)?.peakRSSI.toString()
        CoroutineScope(Dispatchers.IO).launch {
            try {

                Log.e("handleTagdata", code)
                newTag(code)

            } catch (ex: Exception) {

            }
        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        Log.e("hanheldtriggerpress", "${localSharedPreferences.getPauseStatus()}")

        if (!localSharedPreferences.getPauseStatus()) {

            viewModelScope.launch {

                if (pressed) {
                     rfidHandler!!.perform()

                } else {
                    rfidHandler!!.stop()
                }
            }
        }


    }

    override fun handleStartConnect(connected: Boolean) {
        _dialogVisible.postValue(true)
    }



    override fun batteryLevel(level: Int) {
        Log.e("#########model", "$level")
        _percentCharge.postValue(level)
    }

    suspend fun getInventoryList(): List<Inventory> = withContext(
        Dispatchers.IO) {
        var listItems= repository.getInventoryListLogs()
        listItems
    }

    suspend fun getTagsList(): List<Tags> = withContext(
        Dispatchers.IO) {
        var listTags= repository.getTagsList()
        listTags
    }

    suspend fun newTag(epc:String): Tags = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        repository.insertNewTag(
            Tags(
                0,
                "0",
                "0",
                "0",
                "0",
                0,
                epc,
                formatter.format(nowDate)
            )
        )
    }
}