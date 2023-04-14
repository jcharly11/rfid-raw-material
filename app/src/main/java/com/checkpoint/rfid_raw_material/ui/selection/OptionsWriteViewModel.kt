package com.checkpoint.rfid_raw_material.ui.selection

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceInstanceRFID
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.handheld.kt.interfaces.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.preferences.LocalPreferences
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OptionsWriteViewModel(application: Application) : AndroidViewModel(application) ,
    ResponseHandlerInterface, BatteryHandlerInterface {
    private  var  deviceInstanceRFID: DeviceInstanceRFID? = null
    private var repository: DataRepository = DataRepository.getInstance(
        RawMaterialsDatabase.getDatabase(application.baseContext))

    private val _percentCharge: MutableLiveData<Int> = MutableLiveData(0)
    val percentCharge: LiveData<Int> = _percentCharge

    private val _deviceInstanceReady: MutableLiveData<Boolean> = MutableLiveData(false)
    val deviceInstanceReady: LiveData<Boolean> = _deviceInstanceReady


    private var localSharedPreferences: LocalPreferences = LocalPreferences(application)

    private var readNumber: Int = 0



    fun createIntanceDeviceModeRFID(device: Device) {
        if(deviceInstanceRFID != null){
            deviceInstanceRFID!!.clean()

        }
        deviceInstanceRFID =  DeviceInstanceRFID(device.getReaderDevice())
        deviceInstanceRFID!!.setBatteryHandlerInterface(this)
        deviceInstanceRFID!!.setHandlerInterfacResponse(this)
        deviceInstanceRFID!!.setRfidModeRead()
    }
    fun getBatteryLevel(){
        deviceInstanceRFID!!.battery()
    }
    suspend fun newTag(epc:String,readNumb:Int): Tags? = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        try {
            repository.insertNewTag(
                Tags(
                    0,
                    readNumb,
                    "0",
                    "0",
                    "0",
                    "0",
                    0,
                    epc,
                    formatter.format(nowDate)
                )
            )
        }catch (ex: Exception){
            Log.e("Tags Exception", "${ex.message.toString()}")
            null
        }

    }

    override fun handleTagdata(tagData: Array<TagData?>?) {
        val code = tagData?.get(0)?.tagID.toString()
        readNumber= localSharedPreferences.getReadNumber()

        Log.e("handleTagdata","$code")
        try {
            tagData!!.iterator().forEachRemaining {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.e("TRYING INSERT","${it!!.tagID}")
                    newTag(it!!.tagID.toString(),readNumber)

                }
            }
        } catch (ex: Exception) {
            Log.e("handleTagdata Exception",ex.message.toString())


        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (!localSharedPreferences.getPauseStatus()) {
            if (pressed) {
                deviceInstanceRFID!!.perform()
            } else {
                deviceInstanceRFID!!.stop()
            }
        }
    }

    override fun handleStartConnect(connected: Boolean) {
        Log.e("handleStartConnect","$connected")
        _deviceInstanceReady.postValue(connected)
    }

    override fun batteryLevel(level: Int) {
        _percentCharge.postValue(level)
    }
    fun getReadNumber():Int {
        return localSharedPreferences.getReadNumber()
    }
}