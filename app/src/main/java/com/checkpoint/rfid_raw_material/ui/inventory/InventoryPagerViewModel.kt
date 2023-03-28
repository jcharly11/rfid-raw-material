package com.checkpoint.rfid_raw_material.ui.inventory

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkpoint.rfid_raw_material.bluetooth.BluetoothHandler
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
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

@SuppressLint("MissingPermission")
class InventoryPagerViewModel(application: Application) : AndroidViewModel(application),
    ResponseHandlerInterface,
    BatteryHandlerInterface {
    //private var repository: DataRepository
    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)

    private val _dialogVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val dialogVisible: LiveData<Boolean> = _dialogVisible

    private val _percentCharge: MutableLiveData<Int> = MutableLiveData(0)
    val percentCharge: LiveData<Int> = _percentCharge


    private var bluetoothHandler: BluetoothHandler? = null

    private var deviceName: String? = null

    private var idInventory = 0

    private var context: Context = application.baseContext
    private var zebraRFIDHandlerImpl: ZebraRFIDHandlerImpl? = null
    private var maxLevel = 0
    private var repository: DataRepository


    init {

        //repository = DataRepository.getInstance(InventoryDataBase.getDatabase(application.baseContext))
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



        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )

    }

    fun startHandHeld(){
        if (deviceName != null) {
            zebraRFIDHandlerImpl = ZebraRFIDHandlerImpl()
            zebraRFIDHandlerImpl?.listener(this, this)
            zebraRFIDHandlerImpl?.start(getApplication(), 150, deviceName!!, "SESSION_1")
        }

    }

    fun restartHandeldSetNewPower(newPower: Int, session: String) {
        zebraRFIDHandlerImpl?.onDestroy()
        zebraRFIDHandlerImpl?.start(getApplication(), newPower, deviceName!!, session)
    }

    fun setIdInventory(id: Int) {
        idInventory = id
    }

    fun resume() {
        zebraRFIDHandlerImpl?.onPostResume()
    }

    fun destroy() {
        zebraRFIDHandlerImpl?.onDestroy()
    }

    fun callBatteryLevel() {
        zebraRFIDHandlerImpl?.battery()
    }

    fun getCapabilities(): IntArray? {
        return zebraRFIDHandlerImpl?.powerSoupportedList()
    }

    fun currentPower(): Int? {
        return zebraRFIDHandlerImpl?.currentPower()
    }

    override  fun handleTagdata(tagData: Array<TagData?>?) {
        val code = tagData?.get(0)?.tagID.toString()

        Log.e("handleTagdata","$code")

            try {
                 tagData!!.iterator().forEachRemaining {
                        CoroutineScope(Dispatchers.IO).launch {
                            newTag(it!!.tagID.toString())
                        }
                    }

             } catch (ex: Exception) {

            }

    }

    override fun handleTriggerPress(pressed: Boolean) {
        Log.e("hanheldtriggerpress", "${localSharedPreferences.getPauseStatus()}")

        if (!localSharedPreferences.getPauseStatus()) {
            if (pressed) {
                zebraRFIDHandlerImpl?.perform()
            } else {
                zebraRFIDHandlerImpl?.stop()
            }
        }


    }

    suspend fun newTag(epc:String): Tags = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        repository.insertNewTag(
            Tags(
                0,
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
}